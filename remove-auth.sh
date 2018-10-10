#!/usr/bin/env bash

set -e

./remove-demo.sh

rm -r ./src/main/kotlin/app/auth/ || true
rm -r ./src/test/kotlin/app/auth/ || true
rm -r ./src/test/kotlin/featuretests/auth/ || true
rm -r ./src/test/kotlin/templates/emails/* || true
rm -r ./src/main/resources/db/migration/V2__Add_users_table.sql || true
rm -r ./src/main/resources/templates/auth/ || true
rm -r ./src/main/resources/templates/emails/* || true

deleteLineContaining() {
    local file_name="${1}"
    local match="${2}"
    local line_number=$(grep -n "${match}" "${file_name}" | awk -F ":" '{print $1}')
    if [[ ! -z "${line_number}" ]]; then
        sed -i.bak -e "${line_number}d" "${file_name}"
    fi
}

deleteLineContaining ./src/test/kotlin/helpers/FeatureTest.kt "auth.AuthService"
deleteLineContaining ./src/test/kotlin/helpers/FeatureTest.kt "auth.DashboardPage"
deleteLineContaining ./src/test/kotlin/helpers/FeatureTest.kt "auth.ConfirmationPage"
deleteLineContaining ./src/test/kotlin/helpers/FeatureTest.kt "auth.LoginPage"
deleteLineContaining ./src/test/kotlin/helpers/FeatureTest.kt "auth.SignupPage"
deleteLineContaining ./src/test/kotlin/helpers/FeatureTest.kt "auth.ThankYouPage"
deleteLineContaining ./src/test/kotlin/helpers/FeatureTest.kt "delete from users"

deleteLineContainingWithAnnotation() {
    local file_name="${1}"
    local match="${2}"
    local line_number=$(grep -n "${match}" "${file_name}" | awk -F ":" '{print $1}')
    local annotation_line_number=$((line_number-1))
    if [[ ! -z "${line_number}" ]]; then
        sed -i.bak -e "${annotation_line_number}d;${line_number}d" "${file_name}"
    fi
}

deleteLineContainingWithAnnotation ./src/test/kotlin/helpers/FeatureTest.kt "loginPage: LoginPage"
deleteLineContainingWithAnnotation ./src/test/kotlin/helpers/FeatureTest.kt "signupPage: SignupPage"
deleteLineContainingWithAnnotation ./src/test/kotlin/helpers/FeatureTest.kt "thankYouPage: ThankYouPage"
deleteLineContainingWithAnnotation ./src/test/kotlin/helpers/FeatureTest.kt "dashboardPage: DashboardPage"
deleteLineContainingWithAnnotation ./src/test/kotlin/helpers/FeatureTest.kt "confirmationPage: ConfirmationPage"
deleteLineContainingWithAnnotation ./src/test/kotlin/helpers/FeatureTest.kt "authService: AuthService"

rm ./src/test/kotlin/helpers/FeatureTest.kt.bak || true

./gradlew clean