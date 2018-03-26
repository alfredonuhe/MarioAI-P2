#
# Script to test P1BotAgent
#

#Variables
RED='\033[0;31m'
NC='\033[0m'
GREEN='\033[0;32m'
i=1


#Preconditions
if [ "$#" -ne 1 ]; then
    echo -e "${RED}ERROR:${NC} You must enter number of levels to test."
    exit 1
fi

#Code execution
echo -e "Starting the Mario testing...\n"
echo `sh compilar.sh`

for i in `seq 1 $1`
do
    echo `sh ejecutar.sh P1BotAgent -vis off -ls $i &> /dev/null`
done
echo "Successful tests"
echo "${GREEN}[Done]"
exit 0
