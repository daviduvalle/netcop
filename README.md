# Netcop

Netcop is CLI tool that executes parallel HTTP pings to multiple endpoints. The output contains basic stats such as average and standard deviation.

I can be used to troubleshoot network problems or to simply measure the speed of your endpoints.

[Get it here](https://github.com/daviduvalle/netcop/blob/master/out/netcop.jar)

Run it:
```sh
$ java -jar netcop.jar --file endpoints.txt
```

Example output:
```sh
Endpoint                       Samples   Average    Median Deviation
http://www.microsoft.com             4     56.75     41.00     34.31
http://www.google.com                4     87.75     78.00     29.24
http://www.amazon.com                4    106.75     92.50     32.98
http://www.facebook.com              4    115.75    103.00     28.24
http://www.netflix.com               4    122.50    130.00     31.52
```

