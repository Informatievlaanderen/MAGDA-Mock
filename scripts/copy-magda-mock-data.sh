#!/bin/bash
# NOTE: this script will copy the data for a certain number (INSZ/KBO/...) and change all occurances of said number in the new files
# This can be used as a starting point to create new data where there is only a small differences with existing data,
# or when only 1 call is different.

shopt -s globstar
OLD_NUM="$1"
NEW_NUM="$2"
BASE_PATH="magdamock/src/main/resources/magda_simulator"

for file in ${BASE_PATH}/**/${OLD_NUM}.xml; do
  new_file_path="${file/${OLD_NUM}.xml/${NEW_NUM}.xml}"
  cp $file $new_file_path

  sed -i "s/$OLD_NUM/$NEW_NUM/g" $new_file_path
done

echo
echo "Old counts"
grep -rnic $OLD_NUM ${BASE_PATH} | grep -v ':0$'

echo
echo "New counts"
grep -rnic $NEW_NUM ${BASE_PATH} | grep -v ':0$'
