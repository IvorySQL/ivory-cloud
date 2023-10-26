#!/bin/bash

NAMESPACE=$1
STORAGECLASS=$2
ALERTURL=$3

#解压 生成yaml模板
generateYaml(){
  NOWDIR="$( cd $(dirname $0) && pwd )"
  cd $NOWDIR
  sed -i "s/ivory/$NAMESPACE/g" ./kustomization.yaml
  sed -i "s/ReadWriteOnce/$STORAGECLASS/g" ./pvcs.yaml
  sed -i "s/user/$NAMESPACE/g" ./prometheus-config.yaml
  sed -i "s/1.1.1.1:8080/$ALERTURL/g" ./alertmanager-config.yaml
}


generateYaml
exit 0
