{-#LANGUAGE ScopedTypeVariables, ExistentialQuantification#-}
import Utility as U
import qualified Data.Map as Map
main = do
    let vmap0 = Map.singleton "break" (Bool False)
    temp <- getLine
    let vmap1 = Map.insert "a" (Int (read temp)) vmap0
    let vmap2 = Map.insert "b" (Int 1) vmap1
    vmap3 <- go (\vmap0->((vmap0 Map.! "b") U.<= (vmap0 Map.! "a"))) b0 vmap2
    temp <- getLine
    let vmap4 = Map.insert "a" (Int (read temp)) vmap3
    return vmap4

b0 :: Dict -> IO Dict
b0 vmap0 = do
    vmap1 <- if eval(((vmap0 Map.! "a") U.% (vmap0 Map.! "b")) U.== (Int 0))
        then b1 vmap0
        else return vmap0
    let vmap2 = Map.insert "b" ((vmap1 Map.! "b") U.+ (Int 1)) vmap1
    return vmap2

b1 :: Dict -> IO Dict
b1 vmap0 = do
    print (vmap0 Map.! "b");
    return vmap0
