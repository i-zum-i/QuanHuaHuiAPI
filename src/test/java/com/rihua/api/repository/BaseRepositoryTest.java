package com.rihua.api.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;

/**
 * リポジトリテストの基底クラス
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.show-sql=true",
    "spring.jpa.properties.hibernate.format_sql=true",
    "logging.level.org.hibernate.SQL=DEBUG",
    "logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE"
})
public abstract class BaseRepositoryTest {

    @Autowired
    protected TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // テストデータのクリーンアップ
        entityManager.clear();
    }

    /**
     * エンティティを永続化してフラッシュする
     * 
     * @param entity エンティティ
     * @param <T> エンティティタイプ
     * @return 永続化されたエンティティ
     */
    protected <T> T persistAndFlush(T entity) {
        return entityManager.persistAndFlush(entity);
    }

    /**
     * エンティティを永続化する
     * 
     * @param entity エンティティ
     * @param <T> エンティティタイプ
     * @return 永続化されたエンティティ
     */
    protected <T> T persist(T entity) {
        return entityManager.persist(entity);
    }

    /**
     * 変更をフラッシュする
     */
    protected void flush() {
        entityManager.flush();
    }

    /**
     * エンティティマネージャーをクリアする
     */
    protected void clear() {
        entityManager.clear();
    }
}