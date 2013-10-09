import grisu.frontend.control.MonitoringConfig

monitor1 = new MonitoringConfig(
  applications : ['Java'],
  filesToDownload : ['output.zip'],
  targetDir : 'C:\\Users\\Markus\\Desktop\\downloads',
  deleteJob : true,
  postProcessCommand : ["cmd.exe", "\\c", "C:\\Users\\Markus\\Desktop\\test_script.bat"]
)
