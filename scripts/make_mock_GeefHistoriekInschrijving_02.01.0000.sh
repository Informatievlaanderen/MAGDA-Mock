#!/bin/bash

# =====================
# Script Documentation
# =====================
#
# This script extracts all <Inschrijving> elements from XML files in the parent directory and can optionally generate persona files.
#
# Placement:
#   Place this script in the 'GeefHistoriekInschrijving/02.01.0000/script' directory.
#   Make sure xmlstarlet is installed on your system.
#
# Recommended Usage:
#   - Run this script from a Bash shell (Linux, macOS, or Windows WSL2).
#   - Then execute:
#       ./make_mock_GeefHistoriekInschrijving_02.01.0000.sh
#
# Configuration:
#   The script's behavior can be configured using environment variables or the default values in the script:
#
#   - EXTRACT_INSCHRIJVING: When set to 1, extracts <Inschrijving> elements from XML files
#     Example: EXTRACT_INSCHRIJVING=1 ./make_mock_GeefHistoriekInschrijving_02.01.0000.sh
#
#   - CREATE_NEW_PERSONAS: When set to 1, generates persona files with different numbers of inschrijvingen
#     Example: CREATE_NEW_PERSONAS=0 ./make_mock_GeefHistoriekInschrijving_02.01.0000.sh
#
#   - PERSONA_FILES: Space-separated list of XML filenames to generate
#     Example: PERSONA_FILES="50600233706.xml 52121199743.xml" ./make_mock_GeefHistoriekInschrijving_02.01.0000.sh
#
#   - COUNTS: Space-separated list of numbers representing how many inschrijvingen to include in each persona file
#     Example: COUNTS="50 100" ./make_mock_GeefHistoriekInschrijving_02.01.0000.sh
#
# Functionality:
#   - The script will process all .xml files in the parent directory (../) and output extracted
#     <Inschrijving> elements as separate files in the 'inschrijvingen' subdirectory.
#   - The output files will not include the <?xml ...?> header, only the <Inschrijving> element.
#   - If CREATE_NEW_PERSONAS is enabled, the script will generate persona files in the parent directory
#     (default: 50600233706.xml, 52121199743.xml, 54691555728.xml, 55671515945.xml), each based on
#     a template XML file but with the <Inschrijvingen> section replaced by a concatenation of
#     randomly selected extracted <Inschrijving> elements. The number of elements included in each
#     file is determined by the COUNTS variable (default: 99 for each file).
# =====================

# Set default values for configuration variables if environment variables are not set
# Set to 1 to enable Inschrijving extraction, or 0 to disable it
EXTRACT_INSCHRIJVING=${EXTRACT_INSCHRIJVING:-1}

# Set to 1 to enable persona creation, or 0 to disable it
CREATE_NEW_PERSONAS=${CREATE_NEW_PERSONAS:-1}

# Default persona files to be generated if not overridden by environment variables
DEFAULT_PERSONA_FILES="50600233706.xml 52121199743.xml 54691555728.xml 55671515945.xml"
PERSONA_FILES=(${PERSONA_FILES:-$DEFAULT_PERSONA_FILES})

# Default counts of inschrijvingen per persona file if not overridden by environment variables
DEFAULT_COUNTS="99 99 99 99"
COUNTS=(${COUNTS:-$DEFAULT_COUNTS})

# Check if xmlstarlet is installed
if ! command -v xmlstarlet &> /dev/null; then
    echo "Error: xmlstarlet is not installed. This script requires xmlstarlet for XML processing."
    echo "Please install xmlstarlet before running this script."
    echo "On Ubuntu/Debian: sudo apt-get install xmlstarlet"
    echo "On CentOS/RHEL: sudo yum install xmlstarlet"
    echo "On macOS: brew install xmlstarlet"
    exit 1
fi


# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PARENT_DIR="$(dirname "$SCRIPT_DIR")"
XML_DIR="$PARENT_DIR"  # One level up, now points to 02.01.0000
OUTPUT_DIR="$SCRIPT_DIR/inschrijvingen"

# Create output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

echo "Configuration:"
echo "- EXTRACT_INSCHRIJVING: $EXTRACT_INSCHRIJVING"
echo "- CREATE_NEW_PERSONAS: $CREATE_NEW_PERSONAS"
echo "- PERSONA_FILES: ${PERSONA_FILES[*]}"
echo "- COUNTS: ${COUNTS[*]}"
echo ""
echo "Looking for XML files in: $XML_DIR"
echo "Output directory: $OUTPUT_DIR"

# Use local-name() to match Inschrijving elements regardless of namespace or path
if [[ "$EXTRACT_INSCHRIJVING" == "1" ]]; then
    for xml_file in "$XML_DIR"/*.xml; do
        if [[ -f "$xml_file" ]]; then
            echo "Processing file: $(basename "$xml_file")"
            num_elements=$(xmlstarlet sel -t -v 'count(//*[local-name()="Inschrijving"])' "$xml_file" 2>/dev/null || echo 0)
            echo "  Found $num_elements Inschrijving elements"
            for ((i=1; i<=num_elements; i++)); do
                # Corrected: use double quotes so $i is expanded in XPath
                xmlstarlet sel -t -c "//*[local-name()='Inschrijving'][$i]" "$xml_file" 2>/dev/null | \
                xmlstarlet fo --indent-tab 2>/dev/null | \
                awk 'NR>1' > "$OUTPUT_DIR/$(basename "$xml_file" .xml)_inschrijving_${i}.xml"
            done
        fi
    done
fi

# Persona creation section
if [[ "$CREATE_NEW_PERSONAS" == "1" ]]; then
    TEMPLATE_XML="$XML_DIR/00611104284.xml"
    INSC_DIR="$OUTPUT_DIR"
    for idx in ${!PERSONA_FILES[@]}; do
        persona_file="${PERSONA_FILES[$idx]}"
        count="${COUNTS[$idx]:-99}"  # Default to 99 if count is not specified for this index
        # Use find to exclude tmp_persona files, then pipe to shuf for random selection
        selected_files=( $(find "$INSC_DIR" -name "*.xml" ! -name "tmp_persona*" | shuf -n "$((count*2))") )

        # Create a temporary file with all selected inschrijvingen
        tmp_content_file="$XML_DIR/inschrijvingen_content.xml"
        cat /dev/null > "$tmp_content_file"

        # Counter for valid inschrijvingen
        valid_count=0

        # Process each selected file, checking for empty/self-closing tags
        for f in "${selected_files[@]}"; do
            # Check if this file contains a self-closing Inschrijving tag
            if grep -q "<Inschrijving[^>]*/>" "$f"; then
                echo "Skipping self-closing Inschrijving tag in $(basename "$f")"
                continue
            fi

            # Check if the file is empty or too small
            if [[ ! -s "$f" || $(wc -c < "$f") -lt 20 ]]; then
                echo "Skipping empty or too small file: $(basename "$f")"
                continue
            fi

            # Add the valid Inschrijving to our content file
            cat "$f" >> "$tmp_content_file"

            # Increment counter and check if we have enough valid inschrijvingen
            ((valid_count++))
            if [[ $valid_count -ge $count ]]; then
                break
            fi
        done

        # Check if we collected enough inschrijvingen
        if [[ $valid_count -lt $count ]]; then
            echo "Warning: Only found $valid_count valid inschrijvingen, less than requested $count"
        fi

        # Start with the template
        cp "$TEMPLATE_XML" "$XML_DIR/tmp_persona.xml"

        # Remove all existing <Inschrijving> elements under <Inschrijvingen>
        xmlstarlet ed -L -d '//*[local-name()="Inschrijvingen"]/*[local-name()="Inschrijving"]' "$XML_DIR/tmp_persona.xml"

        # Use awk to handle both <Inschrijvingen></Inschrijvingen> and <Inschrijvingen/> cases
        awk -v content_file="$tmp_content_file" '
            # Handle opening <Inschrijvingen> tag (with or without attributes)
            /<Inschrijvingen[^>]*>/ && !/<Inschrijvingen[^>]*\/>/ && !found {
                print $0
                while ((getline line < content_file) > 0) {
                    print line
                }
                close(content_file)
                found = 1
                next
            }
            # Handle self-closing <Inschrijvingen/> tag
            /<Inschrijvingen[^>]*\/>/ && !found {
                # Extract any attributes that might be present
                if (match($0, /<Inschrijvingen([^>]*)\/>/, attr)) {
                    # Print the opening tag with any attributes
                    printf "%s", gensub(/<Inschrijvingen[^>]*\/>/,"<Inschrijvingen" attr[1] ">", "g", $0)
                    # Print the content
                    while ((getline line < content_file) > 0) {
                        print line
                    }
                    close(content_file)
                    # Print the closing tag
                    print "</Inschrijvingen>"
                    found = 1
                    next
                }
            }
            # Skip empty </Inschrijvingen> closing tag if we already processed the content
            /<\/Inschrijvingen>/ && found {
                print $0
                next
            }
            {print}
        ' "$XML_DIR/tmp_persona.xml" > "$XML_DIR/tmp_persona2.xml"

        mv "$XML_DIR/tmp_persona2.xml" "$XML_DIR/tmp_persona.xml"

        # Generate a new GUID
        new_guid=$(uuidgen)

        # Replace both Referte fields with the new GUID and format the output with namespace cleanup
        xmlstarlet ed \
            -u '//*[local-name()="Ontvanger"]/*[local-name()="Referte"]' -v "$new_guid" \
            -u '//*[local-name()="Antwoord"]/*[local-name()="Referte"]' -v "$new_guid" \
            "$XML_DIR/tmp_persona.xml" | xmlstarlet fo --indent-tab -N 2>/dev/null > "$XML_DIR/tmp_persona2.xml"

        # Remove unnecessary xmlns:ns2 attributes
        sed -i 's/ xmlns:ns2="http:\/\/webservice.geefhistoriekinschrijvingdienst-02_01.onderwijs.vlaanderen.be"//g' "$XML_DIR/tmp_persona2.xml"

        # Move the cleaned file to final destination
        mv "$XML_DIR/tmp_persona2.xml" "$XML_DIR/$persona_file"

        # Clean up temporary files
        rm "$XML_DIR/tmp_persona.xml" "$tmp_content_file"
        echo "Created persona file: $persona_file with $count inschrijvingen and GUID $new_guid."
    done
fi

echo "Extracted Inschrijving elements to $OUTPUT_DIR"
