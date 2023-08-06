package penpacket

import (
	"encoding/binary"
	"fmt"
	"math"
)

type PenEvent byte

const (
	HOVER_MOVE  PenEvent = 0x10
	HOVER_EXIT  PenEvent = 0x11
	CONTACT_MOVE PenEvent = 0x20
	CONTACT_DOWN PenEvent = 0x22
	CONTACT_UP   PenEvent = 0x21
    SUPP_ACTION  PenEvent = 0x0F
	DUMMY        PenEvent = 0x00
)

func (pe PenEvent) String() string {
	switch pe {
	case HOVER_MOVE:
		return "HOVER_MOVE"
	case HOVER_EXIT:
		return "HOVER_EXIT"
	case CONTACT_MOVE:
		return "CONTACT_MOVE"
	case CONTACT_DOWN:
		return "CONTACT_DOWN"
	case CONTACT_UP:
		return "CONTACT_UP"
    case SUPP_ACTION:
        return "SUPP_ACTION"
	case DUMMY:
		return "DUMMY"
	default:
		return "UNKNOWN_EVENT"
	}
}

type PenPacket struct {
	Event         PenEvent
	Arg1, Arg2, Arg3 float32
	ButtonPressed bool
}

func (pp *PenPacket) String() string {
	switch pp.Event {
	case HOVER_MOVE:
		return fmt.Sprintf("%s{x: %v, y: %v, %v}", pp.Event, pp.Arg1, pp.Arg2, pp.ButtonPressed)
    case CONTACT_MOVE:
		return fmt.Sprintf("%s{x: %v, y: %v, %v, %v}", pp.Event, pp.Arg1, pp.Arg2, pp.Arg3, pp.ButtonPressed)
	case HOVER_EXIT, CONTACT_DOWN, CONTACT_UP, SUPP_ACTION, DUMMY:
		return fmt.Sprintf("%s{}", pp.Event)
	default:
		return "Unknown Event"
	}
}

func Decode(data []byte) (*PenPacket, error) {
	if len(data) != 14 {
		return nil, fmt.Errorf("Invalid data length. Expected 14, got %v", len(data))
	}

	pp := &PenPacket{
		Event:         PenEvent(data[0]),
		Arg1:          bytesToFloat32(data[1:5]),
		Arg2:          bytesToFloat32(data[5:9]),
		Arg3:          bytesToFloat32(data[9:13]),
		ButtonPressed: data[13] == 0x01,
	}
	
	return pp, nil
}

func bytesToFloat32(b []byte) float32 {
	bits := binary.LittleEndian.Uint32(b)
	float := math.Float32frombits(bits)
	return float
}
