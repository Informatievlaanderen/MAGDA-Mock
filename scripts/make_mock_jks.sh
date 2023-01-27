#!/bin/sh

# This utility script can be used to generate mock .jks key store files (a rather involved procedure to do manually...) for use in unit tests.
# It will store a single certificate + private key in it under the given alias. The result is written to the path provided in option -f.
#
# Example usage: ./make_mock_jks.sh -f output_keystore.jks -a "my alias" -p keypass123 -s keystorepass123
#
# The private key password must consist of at least 4 characters, and the keystore password at least 6 characters.
# You will need to interactively provide some subject information during the certificate signing request stage.
# Multiple aliases can be added to the same file by running the command multiple times, once per alias, with the same -f option.

set -e

notice() {
  echo "$(tput setaf 3)$1$(tput sgr0)" >&2
}

error() {
  echo "$(tput setaf 1)$1$(tput sgr0)" >&2
  exit 1
}

while getopts ":a:f:p:s:" opt; do
  case ${opt} in
    a) key_alias="${OPTARG}" ;;
    f) key_store_file="${OPTARG}" ;;
    p) key_password="${OPTARG}" ;;
    s) key_store_password="${OPTARG}" ;;
  esac
done

[ -n "$key_alias" ] || error "Provide a key alias with -a."
[ -n "$key_store_file" ] || error "Provide an output path for the JKS file with -f."
[ -n "$key_password" ] || error "Provide a private key password with -p."
[ -n "$key_store_password" ] || error "Provide a key store password with -s."

notice "Generating a private key..."
pem=$(mktemp --suffix ".pem")
openssl genrsa -aes256 \
        -passout pass:"$key_password" -out $pem

notice "Creating a certificate signing request..."
csr=$(mktemp --suffix ".csr")
openssl req -new \
        -key $pem -passin pass:"$key_password" \
        -out $csr

notice "Creating a self-signed x509 certificate..."
crt=$(mktemp --suffix ".crt")
openssl x509 -req \
        -in $csr \
        -signkey $pem -passin pass:"$key_password" \
        -out $crt

notice "Exporting PKCS12 store..."
pfx=$(mktemp --suffix ".pfx")
openssl pkcs12 -export \
        -in $crt \
        -name "$key_alias" \
        -inkey $pem -passin pass:"$key_password" \
        -out $pfx -passout pass:"$key_store_password"

notice "Converting PKCS12 store to JKS store..."
keytool -importkeystore \
        -srckeystore $pfx -srcstoretype pkcs12 -srcstorepass "$key_store_password" \
        -destkeystore "$key_store_file" -deststoretype JKS -deststorepass "$key_store_password"

notice "Done."
