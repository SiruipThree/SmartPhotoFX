javac -d bin -cp "lib/*" (Get-ChildItem -Recurse -Filter *.java -Path src/application).FullName
New-Item -ItemType Directory -Force -Path .\bin\resources\
Copy-Item -Path .\resources\*.fxml -Destination .\bin\resources\
