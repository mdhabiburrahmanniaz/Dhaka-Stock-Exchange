<?php


//User “appszonepro_DhakaStockeExchange”
// database “appszonepro_DhakaStockeExchange”
// pass DhakaStockeExchange
 
 
$html = file_get_contents('https://appszonepro.com/apps/DhakaStockExchange/sample.html');

libxml_use_internal_errors(true);
$dom = new DOMDocument;
$dom->loadHTML($html);
libxml_clear_errors();

$Xpath = new DOMXpath($dom);

$values = $Xpath->query('//div[contains(@class, "scroll-item")]//a');

foreach ($values as $item) {
    $text = $item->textContent; // টেক্সট ট্রিম করা হয়েছে
    
    
    $cleanText = str_replace('\xc5\xA0',' ',$text); 
    $cleanText = preg_replace('/\s+/u', ' ', $cleanText);
	$cleanText = trim($cleanText);
	
	$info = explode(' ',$cleanText);
	
	if(count ($info)>=4){
		$name = $info[0]; 
		echo $name;
		
	}
	
   // echo $info . "<br>"; 
}

?>
