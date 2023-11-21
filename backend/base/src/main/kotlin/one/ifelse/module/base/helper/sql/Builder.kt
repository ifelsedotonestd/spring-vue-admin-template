package one.ifelse.module.base.helper.sql

interface Builder<T> {
    fun build(): JpaQueryExecutor<T>
}