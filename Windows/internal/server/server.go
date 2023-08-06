package server

import (
	"context"
	"fmt"
	"log"
	"net"
	"sync"
	"time"

	"github.com/sett17/penferry/internal/penpacket"
	"github.com/sett17/penferry/pkg/synthpointer"
)

var cancel context.CancelFunc
var wg sync.WaitGroup

func Start(ctx context.Context, port string) {
	var childCtx context.Context
	childCtx, cancel = context.WithCancel(ctx)

	log.Println("Starting server on port " + port)

	synthPointer, err := synthpointer.NewSyntheticPointer()
	if err != nil {
		log.Println("Error creating synthetic pointer: " + err.Error())
		return
	}
	log.Println("Synthetic pointer created")

	wg.Add(1)
	go func() {
		defer wg.Done()

		addr, err := net.ResolveUDPAddr("udp", ":"+port)
		if err != nil {
			log.Println("Error resolving address: " + err.Error())
			return
		}

		conn, err := net.ListenUDP("udp", addr)
		if err != nil {
			log.Println("Error starting server: " + err.Error())
			return
		}
		defer conn.Close()

		buffer := make([]byte, 14)

		for {
			conn.SetReadDeadline(time.Now().Add(1 * time.Second))
			_, _, err := conn.ReadFromUDP(buffer)

			select {
			case <-childCtx.Done():
				return
			default:
				if err != nil {
					if opErr, ok := err.(*net.OpError); ok && opErr.Timeout() {
						// Timeout error, ignore it and check context cancellation again
						continue
					} else {
						log.Println("Error reading: " + err.Error())
					}
				} else {
					packet, err := penpacket.Decode(buffer)
					if err != nil {
						log.Println("Error decoding packet: " + err.Error())
					} else {
						log.Println(packet.String())
						err = performAction(synthPointer, packet)
						if err != nil {
							log.Println("Error handling pen event: " + err.Error())
						}
					}
				}
			}
		}
	}()
}

func Stop() {
	if cancel != nil {
		cancel()
	}
	wg.Wait() // wait for goroutine to exit
	log.Println("Stopping server")
}

func performAction(synthPointer *synthpointer.SyntheticPointer, packet *penpacket.PenPacket) error {
	switch packet.Event {
	case penpacket.HOVER_MOVE:
		return synthPointer.HoverMove(packet.Arg1, packet.Arg2, packet.ButtonPressed)
	case penpacket.CONTACT_MOVE:
		return synthPointer.ContactMove(packet.Arg1, packet.Arg2, packet.ButtonPressed, uint32(packet.Arg3))
	case penpacket.HOVER_EXIT:
		return synthPointer.HoverExit()
	case penpacket.CONTACT_DOWN:
		return synthPointer.Down()
	case penpacket.CONTACT_UP:
		return synthPointer.Up()
    case penpacket.SUPP_ACTION:
        return synthPointer.NextScreen()
	default:
		return fmt.Errorf("Unknown pen event: %v", packet.Event)
	}
}
