# Monitor-Bridge
Monitor-Bridge queries performance metrics from your PostgreSQL, Oracle, Redis, ElasticSearch, and MongoDB instances and presents them via JMX. Get your APMs and system monitoring tools like New Relic, AppDynamics, Zabbix, Nagios, or your custom application to poll the data and start improving your service!

## How to build
* 'sbt package' to output a stand alone jar
* 'sbt dist-zip' to output a zip file with all dependencies, scripts and configuration files

## How to run
1. Review bin/settings.sh
2. List your deployed components that you want to monitor in conf/application.conf
3. Run bin/startup
4. Check out 'monitor-bridge' MBean

## How to test
The easiest way to test that your Monitor-Bridge is properly configured and working is to point JConsole and explore the 'monitor-bridge' MBean. Tail logs/monitor.log for errors and exceptions.
