package one.ifelse.module.base.helper.sql

interface TransformBuilder<T> {
    fun map(clazz: Class<T>): QueryExecutor<T>
}