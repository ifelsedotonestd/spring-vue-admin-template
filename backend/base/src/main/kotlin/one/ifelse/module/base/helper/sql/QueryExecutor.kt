package one.ifelse.module.base.helper.sql

interface QueryExecutor<T> {
    fun list(): List<T>

    fun getListResult(): List<T> {
        return list()
    }

    fun unique(): T?

    fun first(): T?

    fun getSingleResult(): T? {
        return unique()
    }

    fun getFirstResult(): T? {
        return first()
    }
}