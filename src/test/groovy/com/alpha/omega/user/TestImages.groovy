package com.alpha.omega.user

import org.testcontainers.utility.DockerImageName

interface TestImages {
    DockerImageName POSTGRES_TEST_IMAGE = DockerImageName.parse("postgres:9.6.12");
    DockerImageName REDIS_TEST_IMAGE = DockerImageName.parse("redis:6.2-alpine");
}