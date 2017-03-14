module Utility (module Utility) where

import Data.Bits as Bits
import Prelude as P
import qualified Data.Map as Map

data Data = Int Int | Double Double | Bool Bool

type Dict = Map.Map String Data

go :: (Dict -> Data) -> (Dict -> IO Dict) -> Dict -> IO Dict
go expr func vmap | (case expr vmap of Bool a -> a) = func vmap >>= \vmap -> go expr func vmap
go expr func vmap = return vmap

eval :: Data -> Bool
eval (Bool b) = b

class Variable a where
    (+) :: a -> a -> a
    (-) :: a -> a -> a
    (*) :: a -> a -> a
    (/) :: a -> a -> a
    (%) :: a -> a -> a
    (**) :: a -> a -> a
    (.|.) :: a -> a -> a
    (.&.) :: a -> a -> a
    (.^.) :: a -> a -> a
    (.>>.) :: a -> a -> a
    (.<<.) :: a -> a -> a
    bnot :: a -> a
    negate :: a -> a
    (||) :: a -> a -> a
    (&&) :: a -> a -> a
    (==) :: a -> a -> a
    (!=) :: a -> a -> a
    (>) :: a -> a -> a
    (>=) :: a -> a -> a
    (<) :: a -> a -> a
    (<=) :: a -> a -> a
    not :: a -> a
    cast :: String -> a -> a

instance Variable Data where
    Int a + Int b = Int $ a P.+ b
    Int a + Double b = Double $ fromIntegral a P.+ b
    Double a + Int b = Double $ a P.+ fromIntegral b
    Double a + Double b = Double $ a P.+ b

    Int a - Int b = Int $ a P.- b
    Int a - Double b = Double $ fromIntegral a P.- b
    Double a - Int b = Double $ a P.- fromIntegral b
    Double a - Double b = Double $ a P.- b

    Int a * Int b = Int $ a P.* b
    Int a * Double b = Double $ fromIntegral a P.* b
    Double a * Int b = Double $ a P.* fromIntegral b
    Double a * Double b = Double $ a P.* b

    Int a / Int b = Int $ div a b
    Int a / Double b = Double $ fromIntegral a P./ b
    Double a / Int b = Double $ a P./ fromIntegral b
    Double a / Double b = Double $ a P./ b

    Int a % Int b = Int $ mod a b
    Int a % Double b = Double $ fromIntegral a P./ b
    Double a % Int b = Double $ a P./ fromIntegral b
    Double a % Double b = Double $ a P./ b

    Int a ** Int b = Int $ a P.^ b
    Int a ** Double b = Double $ fromIntegral a P.** b
    Double a ** Int b = Double $ a P.** fromIntegral b
    Double a ** Double b = Double $ a P.** b

    Int a .|. Int b = Int $ a Bits..|. b
    Int a .&. Int b = Int $ a Bits..&. b
    Int a .^. Int b = Int $ Bits.xor a b
    Int a .>>. Int b = Int $ Bits.shiftR a b
    Int a .<<. Int b = Int $ Bits.shiftL a b

    bnot (Int a) = Int $ Bits.complement a

    negate (Int a) = Int $ P.negate a
    negate (Double a) = Double $ P.negate a

    Bool a || Bool b = Bool $ a P.|| b
    Bool a && Bool b = Bool $ a P.&& b

    not (Bool a) = Bool $ P.not a

    Int a == Int b = Bool $ a P.== b
    Int a == Double b = Bool $ fromIntegral a P.== b
    Double a == Int b = Bool $ a P.== fromIntegral b
    Double a == Double b = Bool $ a P.== b

    Int a != Int b = Bool $ a P./= b
    Int a != Double b = Bool $ fromIntegral a P./= b
    Double a != Int b = Bool $ a P./= fromIntegral b
    Double a != Double b = Bool $ a P./= b

    Int a > Int b = Bool $ a P.> b
    Int a > Double b = Bool $ fromIntegral a P.> b
    Double a > Int b = Bool $ a P.> fromIntegral b
    Double a > Double b = Bool $ a P.> b

    Int a >= Int b = Bool $ a P.>= b
    Int a >= Double b = Bool $ fromIntegral a P.>= b
    Double a >= Int b = Bool $ a P.>= fromIntegral b
    Double a >= Double b = Bool $ a P.>= b

    Int a < Int b = Bool $ a P.< b
    Int a < Double b = Bool $ fromIntegral a P.< b
    Double a < Int b = Bool $ a P.< fromIntegral b
    Double a < Double b = Bool $ a P.< b

    Int a <= Int b = Bool $ a P.<= b
    Int a <= Double b = Bool $ fromIntegral a P.<= b
    Double a <= Int b = Bool $ a P.<= fromIntegral b
    Double a <= Double b = Bool $ a P.<= b

    cast t (Int a) | t P.== "Int" = Int a
    cast t (Int a) | t P.== "Double" = Double $ fromIntegral a
    cast t (Int a) | t P.== "Bool" = Bool $ a P.== 0
    cast t (Double a) | t P.== "Int" = Int $ round a
    cast t (Double a) | t P.== "Double" = Double a
    cast t (Double a) | t P.== "Bool" = Bool $ a P.== 0
    cast t (Bool a) | t P.== "Int" = Int $ if a then 1 else 0
    cast t (Bool a) | t P.== "Double" = Double $ if a then 1 else 0
    cast t (Bool a) | t P.== "Bool" = Bool a

instance Show Data where
    show (Int a) = show a
    show (Double a) = show a
    show (Bool a) = show a