var  contador =0;
$('.disabledButton').on('click', function(event) {
  //true habilito, false deshabilito 
contador+=1;
	 if(contador==1 ){
		 return true;
	 }else{ 
		 return false;
	 }
    
});