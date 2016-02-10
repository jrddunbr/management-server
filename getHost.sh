#!/bin/bash
hostname=$(nslookup $1 | \
grep "name" | \
cut -f2 | \
grep -o " .*" | \
grep -o "=.*" | \
grep -o " .*" | \
cut -f1 -d"." | \
sed -e 's/^[[:space:]]*//')
echo $hostname > host.txt
