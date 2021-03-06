package argonaut

import Argonaut._, Shapeless._
import derive._
import org.scalacheck.Shapeless._
import shapeless._

import utest._


object SumEncodeTests extends TestSuite {
  import ProductEncodeTests.{ compareEncodeJsons, jsonIs }

  lazy val expectedBaseISEncodeJson =
    MkEncodeJson.productEncodeJson(
      ProductEncodeJson.genericEncodeJson(
        LabelledGeneric[BaseIS],
        Lazy(
          HListProductEncodeJson.hconsEncodeJson(
            Witness('i),
            Strict(IntEncodeJson),
            HListProductEncodeJson.hconsEncodeJson(
              Witness('s),
              Strict(StringEncodeJson),
              HListProductEncodeJson.hnilEncodeJson
            )
          )
        )
      ),
      defaultJsonProductCodecFor
    ).encodeJson

  lazy val expectedBaseDBEncodeJson =
    MkEncodeJson.productEncodeJson(
      ProductEncodeJson.genericEncodeJson(
        LabelledGeneric[BaseDB],
        Lazy(
          HListProductEncodeJson.hconsEncodeJson(
            Witness('d),
            Strict(DoubleEncodeJson),
            HListProductEncodeJson.hconsEncodeJson(
              Witness('b),
              Strict(BooleanEncodeJson),
              HListProductEncodeJson.hnilEncodeJson
            )
          )
        )
      ),
      defaultJsonProductCodecFor
    ).encodeJson

  lazy val expectedBaseLastEncodeJson =
    MkEncodeJson.productEncodeJson(
      ProductEncodeJson.genericEncodeJson(
        LabelledGeneric[BaseLast],
        Lazy(
          HListProductEncodeJson.hconsEncodeJson(
            Witness('c),
            Strict(ProductEncodeTests.expectedSimpleEncodeJson),
            HListProductEncodeJson.hnilEncodeJson
          )
        )
      ),
      defaultJsonProductCodecFor
    ).encodeJson

  lazy val expectedBaseEncodeJson = expectedBaseEncodeJsonFor(defaultJsonSumCodecFor)
  lazy val expectedBaseEncodeJsonTypeField = expectedBaseEncodeJsonFor(JsonSumCodecFor(JsonSumCodec.typeField))
  def expectedBaseEncodeJsonFor(codecFor: JsonSumCodecFor[Base]) =
    MkEncodeJson.sumEncodeJson(
      SumEncodeJson.genericEncodeJson(
        LabelledGeneric[Base],
        Lazy(
          CoproductSumEncodeJson.cconsEncodeJson(
            Witness('BaseDB),
            Strict(expectedBaseDBEncodeJson),
            CoproductSumEncodeJson.cconsEncodeJson(
              Witness('BaseIS),
              Strict(expectedBaseISEncodeJson),
              CoproductSumEncodeJson.cconsEncodeJson(
                Witness('BaseLast),
                Strict(expectedBaseLastEncodeJson),
                CoproductSumEncodeJson.cnilEncodeJson
              )
            )
          )
        )
      ),
      codecFor
    ).encodeJson

  val derivedBaseEncodeJsonTypeField = {
    implicit val codecFor = JsonSumCodecFor[Base](JsonSumCodec.typeField)
    EncodeJson.of[Base]
  }

  val tests = TestSuite {

    'codec {
      'base - {
        compareEncodeJsons(EncodeJson.of[Base], expectedBaseEncodeJson)
      }

      'baseTypeField - {
        compareEncodeJsons(derivedBaseEncodeJsonTypeField, expectedBaseEncodeJsonTypeField)
      }
    }

    'output {
      'base - {
        jsonIs(
          BaseLast(Simple(41, "aa", blah = false)): Base,
          Json.obj(
            "BaseLast" -> Json.obj(
              "c" -> Json.obj(
                "i" -> Json.jNumber(41),
                "s" -> Json.jString("aa"),
                "blah" -> Json.jBool(false)
              )
            )
          )
        )

        jsonIs(
          BaseIS(43, "aa"): Base,
          Json.obj(
            "BaseIS" -> Json.obj(
              "i" -> Json.jNumber(43),
              "s" -> Json.jString("aa")
            )
          )
        )

        jsonIs(
          BaseDB(3.2, false): Base,
          Json.obj(
            "BaseDB" -> Json.obj(
              "d" -> Json.jNumber(3.2),
              "b" -> Json.jBool(false)
            )
          )
        )
      }
    }

  }

}
