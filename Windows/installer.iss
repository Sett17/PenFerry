[Setup]
AppName=PenFerry
AppVersion=1.0
DefaultDirName={pf}\PenFerry

[Files]
Source: "Windows\penferry.exe"; DestDir: "{app}"
Source: "Windows\SynthPointer.dll"; DestDir: "{app}"

[Icons]
Name: "{group}\PenFerry"; Filename: "{app}\penferry.exe"
