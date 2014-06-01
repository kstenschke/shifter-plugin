<?php

    echo __FILE__;
    $x = array('aaa', 'ddd', 'eee', 'eee');
    list($a, $b, $C, $d, $e)    = $x;

    $d  = $e;

    // #A30852
?>