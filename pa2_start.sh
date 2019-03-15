CLASSES=<project_directory>
SCRIPT="cd $CLASSES; java -cp . cs455.scaling.client.Client server-hostname server-port message-frequency"
for ((j=1;j<=$1;j++));
do
    COMMAND='gnome-terminal'
    for i in `cat machine_list`
    do
        echo 'logging into '$i
        OPTION='--tab -e "ssh -t '$i' '$SCRIPT'"'
        COMMAND+=" $OPTION"
    done
    eval $COMMAND &
done