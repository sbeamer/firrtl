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

import firrtl.ir._
import firrtl.Utils.{min, max, pow_minus_one}

import com.typesafe.scalalogging.LazyLogging

/** Definitions and Utility functions for [[ir.PrimOp]]s */
object PrimOps extends LazyLogging {
  /** Addition */
  case object Add extends PrimOp { override def toString = "add" }
  /** Subtraction */
  case object Sub extends PrimOp { override def toString = "sub" }
  /** Multiplication */
  case object Mul extends PrimOp { override def toString = "mul" }
  /** Division */
  case object Div extends PrimOp { override def toString = "div" }
  /** Remainder */
  case object Rem extends PrimOp { override def toString = "rem" }
  /** Less Than */
  case object Lt extends PrimOp { override def toString = "lt" }
  /** Less Than Or Equal To */
  case object Leq extends PrimOp { override def toString = "leq" }
  /** Greater Than */
  case object Gt extends PrimOp { override def toString = "gt" }
  /** Greater Than Or Equal To */
  case object Geq extends PrimOp { override def toString = "geq" }
  /** Equal To */
  case object Eq extends PrimOp { override def toString = "eq" }
  /** Not Equal To */
  case object Neq extends PrimOp { override def toString = "neq" }
  /** Padding */
  case object Pad extends PrimOp { override def toString = "pad" }
  /** Interpret As UInt */
  case object AsUInt extends PrimOp { override def toString = "asUInt" }
  /** Interpret As SInt */
  case object AsSInt extends PrimOp { override def toString = "asSInt" }
  /** Interpret As Clock */
  case object AsClock extends PrimOp { override def toString = "asClock" }
  /** Static Shift Left */
  case object Shl extends PrimOp { override def toString = "shl" }
  /** Static Shift Right */
  case object Shr extends PrimOp { override def toString = "shr" }
  /** Dynamic Shift Left */
  case object Dshl extends PrimOp { override def toString = "dshl" }
  /** Dynamic Shift Right */
  case object Dshr extends PrimOp { override def toString = "dshr" }
  /** Arithmetic Convert to Signed */
  case object Cvt extends PrimOp { override def toString = "cvt" }
  /** Negate */
  case object Neg extends PrimOp { override def toString = "neg" }
  /** Bitwise Complement */
  case object Not extends PrimOp { override def toString = "not" }
  /** Bitwise And */
  case object And extends PrimOp { override def toString = "and" }
  /** Bitwise Or */
  case object Or extends PrimOp { override def toString = "or" }
  /** Bitwise Exclusive Or */
  case object Xor extends PrimOp { override def toString = "xor" }
  /** Bitwise And Reduce */
  case object Andr extends PrimOp { override def toString = "andr" }
  /** Bitwise Or Reduce */
  case object Orr extends PrimOp { override def toString = "orr" }
  /** Bitwise Exclusive Or Reduce */
  case object Xorr extends PrimOp { override def toString = "xorr" }
  /** Concatenate */
  case object Cat extends PrimOp { override def toString = "cat" }
  /** Bit Extraction */
  case object Bits extends PrimOp { override def toString = "bits" }
  /** Head */
  case object Head extends PrimOp { override def toString = "head" }
  /** Tail */
  case object Tail extends PrimOp { override def toString = "tail" }
  /** Interpret as Fixed Point **/
  case object AsFixedPoint extends PrimOp { override def toString = "asFixedPoint" }
  /** Shift Binary Point Left **/
  case object BPShl extends PrimOp { override def toString = "bpshl" }
  /** Shift Binary Point Right **/
  case object BPShr extends PrimOp { override def toString = "bpshr" }
  /** Set Binary Point **/
  case object BPSet extends PrimOp { override def toString = "bpset" }

  private lazy val builtinPrimOps: Seq[PrimOp] =
    Seq(Add, Sub, Mul, Div, Rem, Lt, Leq, Gt, Geq, Eq, Neq, Pad, AsUInt, AsSInt, AsClock, Shl, Shr,
        Dshl, Dshr, Neg, Cvt, Not, And, Or, Xor, Andr, Orr, Xorr, Cat, Bits, Head, Tail, AsFixedPoint, BPShl, BPShr, BPSet)
  private lazy val strToPrimOp: Map[String, PrimOp] = builtinPrimOps.map { case op : PrimOp=> op.toString -> op }.toMap

  /** Seq of String representations of [[ir.PrimOp]]s */
  lazy val listing: Seq[String] = builtinPrimOps map (_.toString)
  /** Gets the corresponding [[ir.PrimOp]] from its String representation */
  def fromString(op: String): PrimOp = strToPrimOp(op)

  // Width Constraint Functions
  def PLUS (w1:Width, w2:Width) : Width = (w1, w2) match {
    case (IntWidth(i), IntWidth(j)) => IntWidth(i + j)
    case _ => PlusWidth(w1, w2)
  }
  def MAX (w1:Width, w2:Width) : Width = (w1, w2) match {
    case (IntWidth(i), IntWidth(j)) => IntWidth(max(i,j))
    case _ => MaxWidth(Seq(w1, w2))
  }
  def MINUS (w1:Width, w2:Width) : Width = (w1, w2) match {
    case (IntWidth(i), IntWidth(j)) => IntWidth(i - j)
    case _ => MinusWidth(w1, w2)
  }
  def POW (w1:Width) : Width = w1 match {
    case IntWidth(i) => IntWidth(pow_minus_one(BigInt(2), i))
    case _ => ExpWidth(w1)
  }
  def MIN (w1:Width, w2:Width) : Width = (w1, w2) match {
    case (IntWidth(i), IntWidth(j)) => IntWidth(min(i,j))
    case _ => MinWidth(Seq(w1, w2))
  }

  // Borrowed from Stanza implementation
  def set_primop_type (e:DoPrim) : DoPrim = {
    //println-all(["Inferencing primop type: " e])
    def t1 = e.args.head.tpe
    def t2 = e.args(1).tpe
    def t3 = e.args(2).tpe
    def w1 = passes.getWidth(e.args.head.tpe)
    def w2 = passes.getWidth(e.args(1).tpe)
    def p1 = t1 match { case FixedType(w, p) => p } //Intentional
    def p2 = t2 match { case FixedType(w, p) => p } //Intentional
    def c1 = IntWidth(e.consts.head)
    def c2 = IntWidth(e.consts(1))
    e copy (tpe = e.op match {
      case Add => (t1, t2) match {
        case (_: UIntType, _: UIntType) => UIntType(PLUS(MAX(w1, w2), IntWidth(1)))
        case (_: UIntType, _: SIntType) => SIntType(PLUS(MAX(w1, MINUS(w2, IntWidth(1))), IntWidth(2)))
        case (_: SIntType, _: UIntType) => SIntType(PLUS(MAX(w2, MINUS(w1, IntWidth(1))), IntWidth(2)))
        case (_: SIntType, _: SIntType) => SIntType(PLUS(MAX(w1, w2), IntWidth(1)))
        case (_: FixedType, _: FixedType) => FixedType(PLUS(PLUS(MAX(p1, p2), MAX(MINUS(w1, p1), MINUS(w2, p2))), IntWidth(1)), MAX(p1, p2))
        case _ => UnknownType
      }
      case Sub => (t1, t2) match {
        case (_: UIntType, _: UIntType) => SIntType(PLUS(MAX(w1, w2), IntWidth(1)))
        case (_: UIntType, _: SIntType) => SIntType(MAX(PLUS(w2, IntWidth(1)), PLUS(w1, IntWidth(2))))
        case (_: SIntType, _: UIntType) => SIntType(MAX(PLUS(w1, IntWidth(1)), PLUS(w2, IntWidth(2))))
        case (_: SIntType, _: SIntType) => SIntType(PLUS(MAX(w1, w2), IntWidth(1)))
        case (_: FixedType, _: FixedType) => FixedType(PLUS(PLUS(MAX(p1, p2),MAX(MINUS(w1, p1), MINUS(w2, p2))),IntWidth(1)), MAX(p1, p2))
        case _ => UnknownType
      }
      case Mul => (t1, t2) match {
        case (_: UIntType, _: UIntType) => UIntType(PLUS(w1, w2))
        case (_: UIntType, _: SIntType) => SIntType(PLUS(w1, w2))
        case (_: SIntType, _: UIntType) => SIntType(PLUS(w1, w2))
        case (_: SIntType, _: SIntType) => SIntType(PLUS(w1, w2))
        case (_: FixedType, _: FixedType) => FixedType(PLUS(w1, w2), PLUS(p1, p2))
        case _ => UnknownType
      }
      case Div => (t1, t2) match {
        case (_: UIntType, _: UIntType) => UIntType(w1)
        case (_: UIntType, _: SIntType) => SIntType(PLUS(w1, IntWidth(1)))
        case (_: SIntType, _: UIntType) => SIntType(w1)
        case (_: SIntType, _: SIntType) => SIntType(PLUS(w1, IntWidth(1)))
        case _ => UnknownType
      }
      case Rem => (t1, t2) match {
        case (_: UIntType, _: UIntType) => UIntType(MIN(w1, w2))
        case (_: UIntType, _: SIntType) => UIntType(MIN(w1, w2))
        case (_: SIntType, _: UIntType) => SIntType(MIN(w1, PLUS(w2, IntWidth(1))))
        case (_: SIntType, _: SIntType) => SIntType(MIN(w1, w2))
        case _ => UnknownType
      }
      case Lt => (t1, t2) match {
        case (_: UIntType, _: UIntType) => Utils.BoolType
        case (_: SIntType, _: UIntType) => Utils.BoolType
        case (_: UIntType, _: SIntType) => Utils.BoolType
        case (_: SIntType, _: SIntType) => Utils.BoolType
        case (_: FixedType, _: FixedType) => Utils.BoolType
        case _ => UnknownType
      }
      case Leq => (t1, t2) match {
        case (_: UIntType, _: UIntType) => Utils.BoolType
        case (_: SIntType, _: UIntType) => Utils.BoolType
        case (_: UIntType, _: SIntType) => Utils.BoolType
        case (_: SIntType, _: SIntType) => Utils.BoolType
        case (_: FixedType, _: FixedType) => Utils.BoolType
        case _ => UnknownType
      }
      case Gt => (t1, t2) match {
        case (_: UIntType, _: UIntType) => Utils.BoolType
        case (_: SIntType, _: UIntType) => Utils.BoolType
        case (_: UIntType, _: SIntType) => Utils.BoolType
        case (_: SIntType, _: SIntType) => Utils.BoolType
        case (_: FixedType, _: FixedType) => Utils.BoolType
        case _ => UnknownType
      }
      case Geq => (t1, t2) match {
        case (_: UIntType, _: UIntType) => Utils.BoolType
        case (_: SIntType, _: UIntType) => Utils.BoolType
        case (_: UIntType, _: SIntType) => Utils.BoolType
        case (_: SIntType, _: SIntType) => Utils.BoolType
        case (_: FixedType, _: FixedType) => Utils.BoolType
        case _ => UnknownType
      }
      case Eq => (t1, t2) match {
        case (_: UIntType, _: UIntType) => Utils.BoolType
        case (_: SIntType, _: UIntType) => Utils.BoolType
        case (_: UIntType, _: SIntType) => Utils.BoolType
        case (_: SIntType, _: SIntType) => Utils.BoolType
        case (_: FixedType, _: FixedType) => Utils.BoolType
        case _ => UnknownType
      }
      case Neq => (t1, t2) match {
        case (_: UIntType, _: UIntType) => Utils.BoolType
        case (_: SIntType, _: UIntType) => Utils.BoolType
        case (_: UIntType, _: SIntType) => Utils.BoolType
        case (_: SIntType, _: SIntType) => Utils.BoolType
        case (_: FixedType, _: FixedType) => Utils.BoolType
        case _ => UnknownType
      }
      case Pad => t1 match {
        case _: UIntType => UIntType(MAX(w1, c1))
        case _: SIntType => SIntType(MAX(w1, c1))
        case _: FixedType => FixedType(MAX(w1, c1), p1)
        case _ => UnknownType
      }
      case AsUInt => t1 match {
        case _: UIntType => UIntType(w1)
        case _: SIntType => UIntType(w1)
        case _: FixedType => UIntType(w1)
        case ClockType => UIntType(IntWidth(1))
        case AnalogType(w) => UIntType(w1)
        case _ => UnknownType
      }
      case AsSInt => t1 match {
        case _: UIntType => SIntType(w1)
        case _: SIntType => SIntType(w1)
        case _: FixedType => SIntType(w1)
        case ClockType => SIntType(IntWidth(1))
        case _: AnalogType => SIntType(w1)
        case _ => UnknownType
      }
      case AsFixedPoint => t1 match {
        case _: UIntType => FixedType(w1, c1)
        case _: SIntType => FixedType(w1, c1)
        case _: FixedType => FixedType(w1, c1)
        case ClockType => FixedType(IntWidth(1), c1)
        case _: AnalogType => FixedType(w1, c1)
        case _ => UnknownType
      }
      case AsClock => t1 match {
        case _: UIntType => ClockType
        case _: SIntType => ClockType
        case ClockType => ClockType
        case _: AnalogType => ClockType
        case _ => UnknownType
      }
      case Shl => t1 match {
        case _: UIntType => UIntType(PLUS(w1, c1))
        case _: SIntType => SIntType(PLUS(w1, c1))
        case _: FixedType => FixedType(PLUS(w1,c1), p1)
        case _ => UnknownType
      }
      case Shr => t1 match {
        case _: UIntType => UIntType(MAX(MINUS(w1, c1), IntWidth(1)))
        case _: SIntType => SIntType(MAX(MINUS(w1, c1), IntWidth(1)))
        case _: FixedType => FixedType(MAX(MAX(MINUS(w1,c1), IntWidth(1)), p1), p1)
        case _ => UnknownType
      }
      case Dshl => t1 match {
        case _: UIntType => UIntType(PLUS(w1, POW(w2)))
        case _: SIntType => SIntType(PLUS(w1, POW(w2)))
        case _: FixedType => FixedType(PLUS(w1, POW(w2)), p1)
        case _ => UnknownType
      }
      case Dshr => t1 match {
        case _: UIntType => UIntType(w1)
        case _: SIntType => SIntType(w1)
        case _: FixedType => FixedType(w1, p1)
        case _ => UnknownType
      }
      case Cvt => t1 match {
        case _: UIntType => SIntType(PLUS(w1, IntWidth(1)))
        case _: SIntType => SIntType(w1)
        case _ => UnknownType
      }
      case Neg => t1 match {
        case _: UIntType => SIntType(PLUS(w1, IntWidth(1)))
        case _: SIntType => SIntType(PLUS(w1, IntWidth(1)))
        case _ => UnknownType
      }
      case Not => t1 match {
        case _: UIntType => UIntType(w1)
        case _: SIntType => UIntType(w1)
        case _ => UnknownType
      }
      case And => (t1, t2) match {
        case (_: SIntType | _: UIntType, _: SIntType | _: UIntType) => UIntType(MAX(w1, w2))
        case _ => UnknownType
      }
      case Or => (t1, t2) match {
        case (_: SIntType | _: UIntType, _: SIntType | _: UIntType) => UIntType(MAX(w1, w2))
        case _ => UnknownType
      }
      case Xor => (t1, t2) match {
        case (_: SIntType | _: UIntType, _: SIntType | _: UIntType) => UIntType(MAX(w1, w2))
        case _ => UnknownType
      }
      case Andr => t1 match {
        case (_: UIntType | _: SIntType) => Utils.BoolType
        case _ => UnknownType
      }
      case Orr => t1 match {
        case (_: UIntType | _: SIntType) => Utils.BoolType
        case _ => UnknownType
      }
      case Xorr => t1 match {
        case (_: UIntType | _: SIntType) => Utils.BoolType
        case _ => UnknownType
      }
      case Cat => (t1, t2) match {
        case (_: UIntType | _: SIntType, _: UIntType | _: SIntType) => UIntType(PLUS(w1, w2))
        case (_: FixedType, _: UIntType| _: SIntType) => FixedType(PLUS(w1, w2), PLUS(p1, w2))
        case (_: UIntType | _: SIntType, _: FixedType) => FixedType(PLUS(w1, w2), p1)
        case (t1, t2) => UnknownType
      }
      case Bits => t1 match {
        case (_: UIntType | _: SIntType) => UIntType(PLUS(MINUS(c1, c2), IntWidth(1)))
        case _: FixedType => UIntType(PLUS(MINUS(c1, c2), IntWidth(1)))
        case _ => UnknownType
      }
      case Head => t1 match {
        case (_: UIntType | _: SIntType | _: FixedType) => UIntType(c1)
        case _ => UnknownType
      }
      case Tail => t1 match {
        case (_: UIntType | _: SIntType | _: FixedType) => UIntType(MINUS(w1, c1))
        case _ => UnknownType
      }
      case BPShl => t1 match {
        case _: FixedType => FixedType(PLUS(w1,c1), PLUS(p1, c1))
        case _ => UnknownType
      }
      case BPShr => t1 match {
        case _: FixedType => FixedType(MINUS(w1,c1), MINUS(p1, c1))
        case _ => UnknownType
      }
      case BPSet => t1 match {
        case _: FixedType => FixedType(PLUS(c1, MINUS(w1, p1)), c1)
        case _ => UnknownType
      }
    })
  }
}
