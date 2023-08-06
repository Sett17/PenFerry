package main

import (
	"context"
	"log"

	"github.com/lxn/walk"
	. "github.com/lxn/walk/declarative"
	"github.com/sett17/penferry/internal/server"
)

var mainWindow *walk.MainWindow
var btn *walk.PushButton
var running bool
var ctx context.Context
var cancel context.CancelFunc

func main() {
	var inLE *walk.LineEdit

	ctx, cancel = context.WithCancel(context.Background())

	if _, err := (MainWindow{
		AssignTo: &mainWindow,
		Title:    "PenFerry",
		Size:     Size{Width: 200, Height: 100},
		MaxSize:     Size{Width: 200, Height: 100},
		MinSize:     Size{Width: 200, Height: 100},
		Layout:   VBox{},
		Children: []Widget{
			LineEdit{
				AssignTo: &inLE,
				Text:     "17420",
				OnTextChanged: func() {
					if running {
						server.Stop()
						btn.SetText("Start")
                        running = false
					}
				}},
			PushButton{
				AssignTo: &btn,
				Text:     "Start",
				OnClicked: func() {
					if running {
						server.Stop()
						ctx, cancel = context.WithCancel(context.Background())
						btn.SetText("Start")
						running = false
					} else {
						server.Start(ctx, inLE.Text())
						btn.SetText("Stop")
						running = true
					}
				},
			},
		},
	}.Run()); err != nil {
		log.Fatal(err)
	}
}
