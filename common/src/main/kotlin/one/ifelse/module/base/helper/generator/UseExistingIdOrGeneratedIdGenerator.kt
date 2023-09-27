package one.ifelse.module.base.helper.generator

import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.enhanced.SequenceStyleGenerator
import org.hibernate.service.ServiceRegistry
import org.hibernate.type.Type
import java.util.*

class UseExistingIdOrGeneratedIdGenerator : SequenceStyleGenerator() {
    override fun configure(type: Type?, params: Properties?, serviceRegistry: ServiceRegistry?) {
        super.configure(type, params, serviceRegistry)
    }

    override fun generate(session: SharedSessionContractImplementor?, `object`: Any?): Any {
        val id = session!!.getEntityPersister(null, `object`).getIdentifier(`object`, session)
        return id ?: super.generate(session, `object`)
    }
}