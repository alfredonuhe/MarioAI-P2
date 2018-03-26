@echo off

if [%1]==[] goto usage
if [%2]==[] goto usage
@echo Usage: %0 loop start %2
call compilar.bat
for /l %%x in (1, 1, %1) do (
   call ejecutar.bat %2 -vis off -ls %%x
)
exit /B 1
:usage
@echo include arguments! number of cicles, agent_name.
@echo Done.
goto :eof