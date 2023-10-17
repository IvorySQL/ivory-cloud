#!/bin/bash

paramList="$@"

NOWDIR="$(cd $(dirname $0) && pwd)"
cd $NOWDIR

file_name=./alertmanager-rules-config.yaml
# 传入属性名和属性值，修改属性
changeFile() {
  line=$(grep -n "alert: $1" ./alertmanager-rules-config.yaml | cut -d ":" -f 1)
  let thresholdLine=line+1
  let durationLine=line+2
  let summaryLine=line+10

  #  echo $thresholdStr | sed 's/expr:.+[<>=]=?\s*\d+/'
  thresholdStr=$(sed -n "${thresholdLine}p" $file_name)
  durationStr=$(sed -n "${durationLine}p" $file_name)
  summaryStr=$(sed -n "${summaryLine}p" $file_name)

  newThresholdStr=$(echo "$thresholdStr" | sed -E 's/(.*)(>|<|=|>=|<=)(.*)/\1\2/')" $2"
  newDurationStr=${durationStr%:*}": $3"
  newSummaryStr=$(echo "$summaryStr" | sed -E 's/(.*)(>|<|=|>=|<=)(.*)/\1\2/')" $2%'"

  sed -i "${thresholdLine}s~.*~$newThresholdStr~" $file_name
  sed -i "${durationLine}s~.*~$newDurationStr~" $file_name
  sed -i "${summaryLine}s~.*~$newSummaryStr~" $file_name

#  awk -v line=${thresholdLine} -v text="$newThresholdStr" 'NR==line {$0=text} {print > "./alertmanager-rules-config.yaml"}' $file_name
#  awk -v line=${durationLine} -v text="$newDurationStr" 'NR==line {$0=text} {print > "./alertmanager-rules-config.yaml"}' $file_name
}

for i in $paramList; do
  echo "modify alert rule : $i"
  PLD_IFS="$IFS"
  IFS="*"
  arr=($i)
  IFS="$OLD_IFS"

  alertName=${arr[0]}
  threshold=${arr[1]}
  duration=${arr[2]}

  if [[ -z "$alertName" ]]; then
    continue
  elif [[ -z "$threshold" ]]; then
    continue
  elif [[ -z "$duration" ]]; then
    continue
  fi

  changeFile $alertName $threshold "${duration}s"
done

exit 0
