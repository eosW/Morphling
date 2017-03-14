{-#LANGUAGE ScopedTypeVariables, ExistentialQuantification#-}
import Utility as U
import qualified Data.Map as Map
main = do
    let vmap0 = Map.singleton "break" (Bool False)
    temp <- getLine
    let vmap1 = Map.insert "a" (Int (read temp)) vmap0
    vmap2 <- if eval((vmap1 Map.! "a") U.< (Int 0))
        then b0 vmap1
        else if eval((vmap1 Map.! "a") U.> (Int 100))
        then b1 vmap1
        else if eval(Bool True)
        then b2 vmap1
        else return vmap1
    print (vmap2 Map.! "a");
    return vmap2

b0 :: Dict -> IO Dict
b0 vmap0 = do
    let vmap1 = Map.insert "a" (U.negate (vmap0 Map.! "a")) vmap0
    return vmap1

b1 :: Dict -> IO Dict
b1 vmap0 = do
    vmap1 <- go (\vmap0->((vmap0 Map.! "a") U.> (Int 5))) b3 vmap0
    return vmap1

b2 :: Dict -> IO Dict
b2 vmap0 = do
    vmap1 <- go (\vmap0->(Bool True)) b4 vmap0
    return vmap1

b3 :: Dict -> IO Dict
b3 vmap0 = do
    let vmap1 = Map.insert "a" ((vmap0 Map.! "a") U./ (Int 2)) vmap0
    return vmap1

b4 :: Dict -> IO Dict
b4 vmap0 = do
    let vmap1 = Map.insert "a" ((vmap0 Map.! "a") U.- (Int 1)) vmap0
    vmap2 <- if eval((((vmap1 Map.! "a") U.% (Int 3)) U.== (Int 0)) U.&& ((vmap1 Map.! "a") U.< (Int 0)))
        then b5 vmap1
        else return vmap1
    return vmap2

b5 :: Dict -> IO Dict
b5 vmap0 = do
    let vmap1 = Map.insert "break" (Bool True) vmap0
    return vmap1
