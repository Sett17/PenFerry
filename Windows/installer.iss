[Setup]
AppName=PenFerry
AppVersion=1.1.1
DefaultDirName={commonpf}\PenFerry
OutputBaseFilename=PenFerryInstaller

[Files]
Source: "penferry.exe"; DestDir: "{app}"
Source: "SynthPointer.dll"; DestDir: "{app}"

[Icons]
Name: "{group}\PenFerry"; Filename: "{app}\penferry.exe"
