package com.alpha.omega.user.service

import org.hibernate.dialect.function.ListaggStringAggEmulation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification

class RandomPasswordGeneratorTest extends Specification {
    private static final Logger logger = LoggerFactory.getLogger(RandomPasswordGeneratorTest.class);

    /*
    ./mvnw clean test -Dtest=RandomPasswordGeneratorTest
     */

    def generate_random_password() {
        given:
        String creds = RandomPasswordGenerator.generateCommonsLang3Password(count)
        expect:
        creds instanceof String
        creds.length() == count
        logger.info("Creds length => {}", creds)
        where:
        count | isString
        16    | true
        32    | true
        36    | true
    }

}
