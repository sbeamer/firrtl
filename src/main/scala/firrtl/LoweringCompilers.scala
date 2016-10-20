/*
Copyright (c) 2014 - 2016 The Regents of the University of
California (Regents). All Rights Reserved.  Redistribution and use in
source and binary forms, with or without modification, are permitted
provided that the following conditions are met:
   * Redistributions of source code must retain the above
     copyright notice, this list of conditions and the following
     two paragraphs of disclaimer.
   * Redistributions in binary form must reproduce the above
     copyright notice, this list of conditions and the following
     two paragraphs of disclaimer in the documentation and/or other materials
     provided with the distribution.
   * Neither the name of the Regents nor the names of its contributors
     may be used to endorse or promote products derived from this
     software without specific prior written permission.
IN NO EVENT SHALL REGENTS BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
REGENTS HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
REGENTS SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE. THE SOFTWARE AND ACCOMPANYING DOCUMENTATION, IF
ANY, PROVIDED HEREUNDER IS PROVIDED "AS IS". REGENTS HAS NO OBLIGATION
TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR
MODIFICATIONS.
*/

package firrtl

sealed abstract class CoreTransform extends PassBasedTransform

final case object ChirrtlToHighFirrtlId extends TransformId
final case object IRToWorkingIRId extends TransformId
final case object ResolveAndCheckId extends TransformId
final case object HighFirrtlToMiddleFirrtlId extends TransformId
final case object MiddleFirrtlToLowFirrtlId extends TransformId
final case object LowFirrtlOptimizationId extends TransformId

/** This transforms "CHIRRTL", the chisel3 IR, to "Firrtl". Note the resulting
  * circuit has only IR nodes, not WIR.
  * TODO(izraelevitz): Create RenameMap from RemoveCHIRRTL
  */
class ChirrtlToHighFirrtl extends CoreTransform {
  def inputForm = ChirrtlForm
  def outputForm = HighForm
  override def transformId = ChirrtlToHighFirrtlId
  def passSeq = Seq(
    passes.CheckChirrtl,
    passes.CInferTypes,
    passes.CInferMDir,
    passes.RemoveCHIRRTL)
}

/** Converts from the bare intermediate representation (ir.scala)
  * to a working representation (WIR.scala)
  */
class IRToWorkingIR extends CoreTransform {
  def inputForm = HighForm
  def outputForm = HighForm
  override def transformId = IRToWorkingIRId
  def passSeq = Seq(passes.ToWorkingIR)
}

/** Resolves types, kinds, and genders, and checks the circuit legality.
  * Operates on working IR nodes and high Firrtl.
  */
class ResolveAndCheck extends CoreTransform {
  def inputForm = HighForm
  def outputForm = HighForm
  override def transformId = ResolveAndCheckId
  def passSeq = Seq(
    passes.CheckHighForm,
    passes.ResolveKinds,
    passes.InferTypes,
    passes.CheckTypes,
    passes.Uniquify,
    passes.ResolveKinds,
    passes.InferTypes,
    passes.ResolveGenders,
    passes.CheckGenders,
    passes.InferWidths,
    passes.CheckWidths)
}

/** Expands aggregate connects, removes dynamic accesses, and when
  * statements. Checks for uninitialized values. Must accept a
  * well-formed graph.
  * Operates on working IR nodes.
  */
class HighFirrtlToMiddleFirrtl extends CoreTransform {
  def inputForm = HighForm
  def outputForm = MidForm
  override def transformId = HighFirrtlToMiddleFirrtlId
  def passSeq = Seq(
    passes.PullMuxes,
    passes.ReplaceAccesses,
    passes.ExpandConnects,
    passes.RemoveAccesses,
    passes.ExpandWhens,
    passes.CheckInitialization,
    passes.ResolveKinds,
    passes.InferTypes,
    passes.ResolveGenders,
    passes.InferWidths,
    passes.CheckWidths)
}

/** Expands all aggregate types into many ground-typed components. Must
  * accept a well-formed graph of only middle Firrtl features.
  * Operates on working IR nodes.
  * TODO(izraelevitz): Create RenameMap from RemoveCHIRRTL
  */
class MiddleFirrtlToLowFirrtl extends CoreTransform {
  def inputForm = MidForm
  def outputForm = LowForm
  override def transformId = MiddleFirrtlToLowFirrtlId
  def passSeq = Seq(
    passes.LowerTypes,
    passes.ResolveKinds,
    passes.InferTypes,
    passes.ResolveGenders,
    passes.InferWidths,
    passes.ConvertFixedToSInt)
}

/** Runs a series of optimization passes on LowFirrtl
  * @note This is currently required for correct Verilog emission
  * TODO Fix the above note
  */
class LowFirrtlOptimization extends CoreTransform {
  def inputForm = LowForm
  def outputForm = LowForm
  override def transformId = LowFirrtlOptimizationId
  def passSeq = Seq(
    passes.RemoveValidIf,
    passes.ConstProp,
    passes.PadWidths,
    passes.ConstProp,
    passes.Legalize,
    passes.ConstProp,
    passes.SplitExpressions,
    passes.CommonSubexpressionElimination,
    passes.DeadCodeElimination)
}


import TransformUtils.getLoweringTransforms

/** Emits input circuit
  * Will replace Chirrtl constructs with Firrtl
  */
class HighFirrtlCompiler extends Compiler {
  def emitter = new FirrtlEmitter
  def transforms: Seq[Transform] = getLoweringTransforms(ChirrtlForm, HighForm)
}

/** Emits lowered input circuit */
class LowFirrtlCompiler extends Compiler {
  def emitter = new FirrtlEmitter
  def transforms: Seq[Transform] = getLoweringTransforms(ChirrtlForm, LowForm)
}

/** Emits Verilog */
class VerilogCompiler extends Compiler {
  def emitter = new VerilogEmitter
  def transforms: Seq[Transform] =
    getLoweringTransforms(ChirrtlForm, LowForm) :+ (new LowFirrtlOptimization)
}
