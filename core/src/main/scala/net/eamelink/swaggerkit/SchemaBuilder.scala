package net.eamelink.swaggerkit

/**
 * Trait with methods for building Schemas
 */
trait SchemaBuilder {
  implicit def typeToProperty(st: Type): Property = Property(st)
}