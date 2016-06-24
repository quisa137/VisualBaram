var x=0,y=0;
$(".dc-legend-item").each(function(i,item){
  switch(i%3){
    case 0:
      x=0;
      if(i>0){y=y+18;}
    break;
    default:
       x=x+54;
    break;
   };
    $(item).attr("transform","translate("+x+","+y+")");
  }
);