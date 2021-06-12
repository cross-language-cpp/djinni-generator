package djinni

import djinni.generatorTools.Spec

class NodeJsHelperFilesDescriptor(spec: Spec) {
  val ObjectWrapperName = spec.nodeIdentStyle.ty(NodeJsHelperFilesDescriptor.objectWrapperLogicalName)
  val ObjectWrapperHeader = ObjectWrapperName + "." + spec.cppHeaderExt

  val HexUtilsName = spec.nodeIdentStyle.ty(NodeJsHelperFilesDescriptor.hexUtilsLogicalName)
  val HexUtilsHeader = HexUtilsName + "." + spec.cppHeaderExt
  val HexUtilsCpp = HexUtilsName + "." + spec.cppExt
}

object NodeJsHelperFilesDescriptor {
  protected val objectWrapperLogicalName = "ObjectWrapper"
  protected val hexUtilsLogicalName = "HexUtils"
}
