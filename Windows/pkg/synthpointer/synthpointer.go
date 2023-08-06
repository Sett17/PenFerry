package synthpointer

import (
	"fmt"
	"syscall"
	"unsafe"
)

type SyntheticPointer struct {
	dll                       *syscall.DLL
	createSynthPointer        *syscall.Proc
	getDefaultPointerTypeInfo *syscall.Proc
	hoverMove                 *syscall.Proc
	contactMove               *syscall.Proc
	hoverExit                 *syscall.Proc
	down                      *syscall.Proc
	up                        *syscall.Proc
	device                    uintptr
	info                      uintptr
	currentScreen             uintptr
	screenCount               int
}

func NewSyntheticPointer() (*SyntheticPointer, error) {
	dll, err := syscall.LoadDLL("SynthPointer.dll")
	if err != nil {
		return nil, err
	}

	createSynthPointer, err := dll.FindProc("CreateSynthPointer")
	if err != nil {
		return nil, err
	}

	getDefaultPointerTypeInfo, err := dll.FindProc("GetDefaultPointerTypeInfo")
	if err != nil {
		return nil, err
	}

	hoverMove, err := dll.FindProc("HoverMove")
	if err != nil {
		return nil, err
	}

	contactMove, err := dll.FindProc("ContactMove")
	if err != nil {
		return nil, err
	}

	hoverExit, err := dll.FindProc("HoverExit")
	if err != nil {
		return nil, err
	}

	down, err := dll.FindProc("Down")
	if err != nil {
		return nil, err
	}

	up, err := dll.FindProc("Up")
	if err != nil {
		return nil, err
	}

	screenCountProc, err := dll.FindProc("ScreenCount")
	if err != nil {
		return nil, err
	}
	var screenCount int
	_, _, err = screenCountProc.Call(uintptr(unsafe.Pointer(&screenCount)))
	if err != nil && err.Error() != "The operation completed successfully." {
		return nil, err
	}

	// Print the screen count
	fmt.Println("Screen count:", screenCount)

	device, _, _ := createSynthPointer.Call()
	info, _, _ := getDefaultPointerTypeInfo.Call()

	return &SyntheticPointer{
		dll:                       dll,
		createSynthPointer:        createSynthPointer,
		getDefaultPointerTypeInfo: getDefaultPointerTypeInfo,
		hoverMove:                 hoverMove,
		contactMove:               contactMove,
		hoverExit:                 hoverExit,
		down:                      down,
		up:                        up,
		device:                    device,
		info:                      info,
		screenCount:               screenCount,
	}, nil
}

func (sp *SyntheticPointer) HoverMove(x, y float32, buttonPressed bool) error {
	_, _, err := sp.hoverMove.Call(sp.device, sp.info, uintptr(*(*uint32)(unsafe.Pointer(&x))), uintptr(*(*uint32)(unsafe.Pointer(&y))), sp.currentScreen, uintptr(boolToUintptr(buttonPressed)))
	if err != syscall.Errno(0) {
		return err
	}
	return nil
}

func (sp *SyntheticPointer) ContactMove(x, y float32, buttonPressed bool, pressure uint32) error {
	_, _, err := sp.contactMove.Call(sp.device, sp.info, uintptr(*(*uint32)(unsafe.Pointer(&x))), uintptr(*(*uint32)(unsafe.Pointer(&y))), sp.currentScreen, uintptr(boolToUintptr(buttonPressed)), uintptr(pressure))
	if err != syscall.Errno(0) {
		return err
	}
	return nil
}

func (sp *SyntheticPointer) HoverExit() error {
	_, _, err := sp.hoverExit.Call(sp.device, sp.info)
	if err != syscall.Errno(0) {
		return err
	}
	return nil
}

func (sp *SyntheticPointer) Down() error {
	_, _, err := sp.down.Call(sp.device, sp.info)
	if err != syscall.Errno(0) {
		return err
	}
	return nil
}

func (sp *SyntheticPointer) Up() error {
	_, _, err := sp.up.Call(sp.device, sp.info)
	if err != syscall.Errno(0) {
		return err
	}
	return nil
}

func (sp *SyntheticPointer) NextScreen() error {
	if sp.currentScreen == uintptr(sp.screenCount)-1 {
		sp.currentScreen = 0
	} else {
		sp.currentScreen++
	}
	return nil
}

func boolToUintptr(value bool) uintptr {
	if value {
		return 1
	}
	return 0
}
