package de.dnpm.dip.rd.validation.api


import de.dnpm.dip.service.auth._
import de.dnpm.dip.service.validation.{
  ValidationPermissions,
  ValidationRoles
}



object RDValidationPermissions extends ValidationPermissions("RD")


class RDValidationPermissionsSPI extends PermissionsSPI
{
  override def getInstance: Permissions =
    RDValidationPermissions
}


object RDValidationRoles extends ValidationRoles(RDValidationPermissions)


class RDValidationRolesSPI extends RolesSPI
{
  override def getInstance: Roles =
    RDValidationRoles
}

