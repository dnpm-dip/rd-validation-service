package de.dnpm.dip.rd.validation.api


import de.dnpm.dip.service.auth._
import de.dnpm.dip.service.validation.ValidationPermissions



object RDValidationPermissions extends ValidationPermissions("RD")


class RDValidationPermissionsSPI extends PermissionsSPI
{
  override def getInstance: Permissions =
    RDValidationPermissions
}



object RDValidationRoles extends Roles
{

  val BasicRDMember =
    Role(
      "RD-Documentarist",
      RDValidationPermissions.permissions
    )

  override val roles: Set[Role] =
    Set(BasicRDMember)

}


class RDValidationRolesSPI extends RolesSPI
{
  override def getInstance: Roles =
    RDValidationRoles
}

