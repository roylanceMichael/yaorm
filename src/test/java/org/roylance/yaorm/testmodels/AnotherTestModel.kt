package org.roylance.yaorm.testmodels

import org.roylance.yaorm.models.IEntity

/**
 * Created by mikeroylance on 10/27/15.
 */
public class AnotherTestModel (
    public override var id:String="",
    public var description:String="",
    public var gram:String="") : IEntity<String> {
}
