## Data Collection API

Please refer to the documentation site. (TODO: add link)

## For Development Use only:

### Start Data API without bin/pio

```
$ sbt/sbt "data/compile"
$ set -a
$ source conf/pio-env.sh
$ set +a
$ sbt/sbt "data/run-main io.prediction.data.api.Run"
```

### Very simple test

```
$ data/test.sh <appId>
```

### Upgrade from 0.8.0/0.8.1 to 0.8.2

Experimental upgrade tool (Upgrade HBase schema from 0.8.0/0.8.1 to 0.8.2)
Create an app to store the data
```
$ bin/pio app new <my app>
```

Replace <to app ID> by the returned app ID:
(<from app ID> is the original app ID used in 0.8.0/0.8.2.)

```
$ set -a
$ source conf/pio-env.sh
$ set +a
$ sbt/sbt "data/run-main io.prediction.data.storage.hbase.upgrade.Upgrade <from app ID>" "<to app ID>"
```

### Upgrade from 0.8.2 to 0.8.3

0.8.3 disallow entity types `pio_user` and `pio_item`. These types are used by
default for most SDKs. We deprecate the use in 0.8.3, and SDKs helper functions
use `user` and `item` instead respectively.

This script performs the migration by copying one appId to another. User can
either point the engine to the new appId, or can migrate the data back to the
old one using hbase import / export tool.

Suppose we are migrating `<old_app_id>`.

```
$ set -a
$ source conf/pio-env.sh
$ set +a
$ bin/pio app new NewApp
... you will see <new_app_id> 
$ sbt/sbt "data/run-main io.prediction.data.storage.hbase.upgrade.Upgrade_0_8_3 <old_app_id> <new_app_id>"
... Done.
```

`<new_app_id>` must be empty when you upgrade. You can check the status of an
app using:

```
$ sbt/sbt "data/run-main io.prediction.data.storage.hbase.upgrade.CheckDistribution <new_app_id>"
```

If it shows that it is non-empty, you can clean it with

```
$ bin/pio app data-delete <new_app_name>
```


