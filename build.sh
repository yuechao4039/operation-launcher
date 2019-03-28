#!/bin/bash
echo =================================
echo 自动化部署脚本启动
echo 自动化部署脚本启动
echo =================================

LAUNCHER=/root/workspace/webapp-launcher
CATALINA_HOME=/root/tomcat
FE_BASE=/root/workspace/fe-base
FE_FRAME=/root/workspace/financial-fe-framework

echo git pull start
cd $LAUNCHER; git pull
cd $FE_BASE; git pull
cd $FE_FRAME; git pull
echo git pull end

echo build fe start
cd $FE_FRAME
if [ -e $FE_FRAME/node_modules ];
	then rm -rf $FE_FRAME/node_modules;
fi;
npm i; npm run build;
echo build fe end


echo build launcher start
cd $LAUNCHER
/usr/bin/cp -fR $FE_FRAME/dist/* /$LAUNCHER/src/main/webapp
mvn clean install -Dmaven.test.skip=true
echo build launcher end


echo 关闭tomcat服务器
ps -ef | grep tomcat | grep -v grep | awk '{print $2}'  | sed -e "s/^/kill -9 /g" | sh -
echo 删除以往文件
if [ -e $CATALINA_HOME/ROOT ];
	then rm -rf $CATALINA_HOME/ROOT;
fi;
if [ -e $CATALINA_HOME/ROOT.war ];
	then rm -rf $CATALINA_HOME/ROOT.war;
fi;
echo 移动文件
/usr/bin/mv -f /$LAUNCHER/target/*.war $CATALINA_HOME/webapps/ROOT.war
echo 重启服务器
sh $CATALINA_HOME/bin/startup.sh
echo =================================
echo finish
echo =================================
