package argonaut

import argonaut.Argonaut.ToJsonIdentity
import scalaz.Scalaz.ToEitherOps

import org.scalacheck.{ Arbitrary, Prop }
import shapeless.test.illTyped
import utest._
import Util._


object ShapelessTests extends TestSuite {
  private def toFromJson[T: EncodeJson : DecodeJson](t: T): DecodeResult[T] = t.asJson.as[T]

  private def sameAfterBeforeSerialization[T: Arbitrary : EncodeJson : DecodeJson]: Unit =
    Prop.forAll {
      t: T =>
        toFromJson(t).result == t.right
    }.validate

  import org.scalacheck.Shapeless._
  import Shapeless._

  val tests = TestSuite {
    'serializeDeserialize {
      'empty - {
        sameAfterBeforeSerialization[Empty.type]
      }

      'emptyCC - {
        sameAfterBeforeSerialization[EmptyCC]
      }

      'simple - {
        sameAfterBeforeSerialization[Simple]
      }

      'composed - {
        sameAfterBeforeSerialization[Composed]
      }

      'twiceComposed - {
        sameAfterBeforeSerialization[TwiceComposed]
      }

      'composedOptList - {
        sameAfterBeforeSerialization[ComposedOptList]
      }

      'nowThree - {
        sameAfterBeforeSerialization[NowThree]
      }

      'oi - {
        sameAfterBeforeSerialization[OI]
      }

      'oiLoose - {
        val json = Parse.parseOption("{}").get
        // assert macro crashes if result is substituted by its value below
        val result = json.as[OI].result
        assert(result == OI(None).right)
      }

      'base - {
        sameAfterBeforeSerialization[Base]
      }

      'simpleWithJsDummy - {
        EncodeJson.of[Json]
        DecodeJson.of[Json]
        EncodeJson.of[SimpleWithJs]
        DecodeJson.of[SimpleWithJs]
        // Arbitrary[SimpleWithJs] doesn't seem fine
        // sameAfterBeforeSerialization[SimpleWithJs]
      }
    }
  }

  illTyped(" EncodeJson.of[NoArbitraryType] ")
  illTyped(" DecodeJson.of[NoArbitraryType] ")
  illTyped(" EncodeJson.of[ShouldHaveNoArb] ")
  illTyped(" DecodeJson.of[ShouldHaveNoArb] ")
  illTyped(" EncodeJson.of[ShouldHaveNoArbEither] ")
  illTyped(" DecodeJson.of[ShouldHaveNoArbEither] ")
  illTyped(" EncodeJson.of[BaseNoArb] ")
  illTyped(" DecodeJson.of[BaseNoArb] ")

}
