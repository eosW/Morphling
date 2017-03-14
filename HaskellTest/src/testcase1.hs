{-#LANGUAGE ScopedTypeVariables, ExistentialQuantification#-}
import Utility as U
import qualified Data.Map as Map
main = do
    let vmap0 = Map.empty
    temp <- getLine
    let vmap1 = Map.insert "a" (Double (read temp)) vmap0
    let vmap2 = Map.insert "a" ((vmap1 Map.! "a") U.** (Int 2)) vmap1;
    let vmap3 = Map.insert "b" ((cast "Int" ((vmap2 Map.! "a") U.+ (Int 1))) U..>>. (Int 2)) vmap2;
    print (vmap3 Map.! "b");
