package com.example.visabreno.support;

import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Path;

public final class TestPostgres {

    public static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17-alpine")
                    .withDatabaseName("visa_breno_test")
                    .withUsername("test")
                    .withPassword("test")
                    .withCopyFileToContainer(
                            MountableFile.forHostPath(Path.of("init.sql").toAbsolutePath()),
                            "/docker-entrypoint-initdb.d/001-init.sql"
                    );

    static {
        POSTGRES.start();
    }

    private TestPostgres() {
    }
}
