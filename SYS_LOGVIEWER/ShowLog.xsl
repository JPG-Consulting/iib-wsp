<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" encoding="UTF-8" indent="yes" standalone="yes"/>

	<xsl:template match="/">
		<html>
		<meta name="viewport" content="width=device-width, initial-scale=1"></meta>
		<meta http-equiv="X-UA-Compatible" content="IE=EDGE"></meta>
		<link rel="stylesheet" href="https://www.w3schools.com/lib/w3.css"></link>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
		<link rel="stylesheet" href="https://www.w3schools.com/lib/w3-theme-blue-grey.css"></link>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"></link>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/stupidtable/1.0.3/stupidtable.min.js"></script>
		
			
			
			<body>
				<div class="w3-bar w3-theme w3-xlarge">
				  <a class="w3-bar-item w3-button"><i class="fa fa-bars"></i></a>
				  <span class="w3-bar-item">IIB Log</span>
				</div>
					
				<nav class="w3-topnav w3-theme">
				<div class="w3-bar w3-theme">
				  <div id="dropdown" class="w3-dropdown-hover w3-theme">
				    <button class="w3-button w3-theme w3-hover-white" id="dropdown-name"><h6 id="dropdown-title" style="margin:0;">Flow Name</h6>
				    <div id="flow-list" class="w3-dropdown-content w3-theme">
				      
				    </div>
				    </button>
				  </div>
				  <input id="from-date" type="date" class="w3-bar-item w3-input w3-mobile w3-theme" onkeypress="runSearchOnEnter(event)"></input>
				  <input id="to-date" type="date" class="w3-bar-item w3-input w3-mobile w3-theme" onkeypress="runSearchOnEnter(event)"></input>
				  <div class="w3-bar-item w3-button w3-hover-white" onclick="json_ajax()"><i class="fa fa-search" style="font-size:32"></i></div>
				</div>
				<script>
			
				document.getElementById('from-date').value = new Date().toISOString().slice(0,10);
			
				</script>
				</nav>


				
				<div style="overflow-x:auto; overflow-x:scroll; overflow-y:visible; width: 100vw; height: 80vh">
				<table class="w3-table-all w3-small w3-responsive table-layout: fixed">
					<thead id="header">
						<th data-sort="string">ID <button class="w3-button w3-hover-white"  style="float:right" onclick="sort()"><i class="fa fa-sort"></i></button></th>
						<th data-sort="string">MsgID<button class="w3-button w3-hover-white"  style="float:right" onclick="sort()"><i class="fa fa-sort"></i></button></th>
						<th data-sort="string">Service Name<button class="w3-button w3-hover-white"  style="float:right" onclick="sort()"><i class="fa fa-sort"></i></button></th>
						<th data-sort="string">Flow Name<button class="w3-button w3-hover-white"  style="float:right" onclick="sort()"><i class="fa fa-sort"></i></button></th>
						<th data-sort="string">Description<button class="w3-button w3-hover-white"  style="float:right" onclick="sort()"><i class="fa fa-sort"></i></button></th>
						<th>Has XML</th>
						<th>Has Exception</th>
						<th>XML</th>
						<th>Exception</th>
						<th data-sort="string">Timestamp<button class="w3-button w3-hover-white"  style="float:right" onclick="sort()"><i class="fa fa-sort"></i></button></th>
					
					<tr id="search-bar">
						<th>
							<input class="search-input" type="text" onkeyup="search()" placeholder="Ricerca per ID..."
								title="Digita una stringa" />
						</th>
						<th>
							<input type="text" class="search-input" onkeyup="search()"
								placeholder="Ricerca per messaggio..." title="Digita una stringa" />
						</th>
						<th>
							<input type="text" class="search-input" onkeyup="search()" placeholder="Ricerca per service name..."
								title="Digita una stringa" />
						</th>
						<th>
							<input type="text" class="search-input" onkeyup="search()"
								placeholder="Ricerca per flusso..." title="Digita una stringa" />
						</th>
						<th>
							<input type="text" class="search-input" onkeyup="search()"
								placeholder="Ricerca per descrizione..." title="Digita una stringa" />
						</th>
						<th><input type="text" class="search-input" onkeyup="search()" placeholder="Ricerca per xml..."/></th>
						<th><input type="text" class="search-input" onkeyup="search()" placeholder="Ricerca per eccezione..."/></th>
						<th><input type="text" class="search-input" style="display:none"/></th>
						<th><input type="text" class="search-input" style="display:none"/></th>
						<th>
							<input type="text" class="search-input" onkeyup="search()"
								placeholder="Ricerca per data e ora..." title="Digita una stringa" />
						</th>
					</tr>
					</thead>
					<tbody id="body"></tbody>
				</table>
				</div>
				<div id="xml-modal" class="w3-modal">
				  <div class="w3-modal-content" style="height: 70vh; width:70wh">
				  <div class="w3-container w3-theme"><h5>XML TEXT <button onclick="document.getElementById('xml-modal').style.display='none'" 
				      class="w3-closebtn w3-theme" style="padding-right : 10px; border:none"><i class="fa fa-window-close-o"></i></button>
				      <button onclick="document.getElementById('loading-modal').style.display='block'; decode(document.getElementById('xml-text'));"  
				      class="w3-closebtn w3-theme" style="border:none; font-size:17">DECODE</button>
				      </h5></div>
				  	
				    <div class="w3-container">
				      <pre lang='xml' id="xml-text" style='height:85%; width:100%; overflow:auto; display:inline-block;'></pre>
				    </div>
				  </div>
				</div>
				
				<div id="loading-modal" class="w3-modal">
				  <div class="w3-modal-content">
				  <header class="w3-container w3-theme"> 
				      <h2>Loading data...</h2>
				  </header>
				
				    <div class="w3-container" style="text-align:center">
				      <i class="fa fa-cog fa-spin fa-3x fa-fw" style="display:inline-block; font-size:150"></i>
				    </div>
				   </div>
				</div>
			</body>
			
			<script language="javascript"><![CDATA[
			$(document).ready(function(){
				$("#flow-list").append('<a class="w3-hover-white dropdown-selected w3-white" onclick="chooseDropdown(this);">All message flows</a>');
				$.ajax({
					    url : '/sys/view_log/get_flows',
						data : '',
						async: true,
						cache : false,
						type : 'get',   
					    dataType : 'json',
					    success: function(xhr) {
					    	for (i=0; i < xhr.length; i++) {
				        		var riga = xhr[i];
					    		var flow = '<a class="w3-hover-white" onclick="chooseDropdown(this)">'+ riga.FLOW_NAME +'</a>';
					    		$("#flow-list").append(flow);
					    	}
					        
					 	}
						});
			});
			
			
			function chooseDropdown(element) {
				$("#dropdown a").removeClass("dropdown-selected w3-white");
				element.className+=" dropdown-selected w3-white";
				$("#dropdown-title").text(element.innerHTML);
				$("#flow-list").removeClass("w3-show");
				json_ajax();
			}
			
			function json_ajax() {
				var flow = document.getElementById("dropdown").getElementsByClassName("dropdown-selected")[0].innerHTML;
				var from = new Date(document.getElementById("from-date").valueAsDate).toISOString().slice(0,10).replace(/-/g,"");
				var to = new Date(document.getElementById("to-date").valueAsDate).toISOString().slice(0,10).replace(/-/g,"");
				var querystring="from="+from+"&to="+to+"&flow="+flow;
				var xmlButton = '<button class="w3-bar-item w3-button w3-hover-white" onclick="fetchXML(this.parentNode.parentNode.getElementsByClassName(&quot;id-log&quot;)[0].innerHTML, &quot;xml&quot;)" ><i class="fa fa-search"></i></button>';
				var exceptionButton = '<button class="w3-bar-item w3-button w3-hover-white" onclick="fetchXML(this.parentNode.parentNode.getElementsByClassName(&quot;id-log&quot;)[0].innerHTML, &quot;exception&quot;)"><i class="fa fa-search" onload="hideIfNull(this, &quot;has-xml&quot;)"></i></button>';
				document.getElementById('loading-modal').style.display='block';
				$.ajax({
				    url : '/sys/view_log/query?'+querystring,
					data : '',
					async: true,
					cache : false,
					type : "get",   
				    dataType : 'json',
				    success: function(xhr) {
				    	$("#body").empty();
				        for (i=0; i < xhr.length; i++) {
				        		var riga = xhr[i];
								$("<tr>")
				        		.append('<td class="id-log">'+
				        		riga.ID_IIB_LOG+"</td><td>"+
				        		riga.ID_MSG+"</td><td>"+
				        		riga.SERVICE_NAME+"</td><td>"+
				        		riga.FLOW_NAME+"</td><td>"+
				        		riga.DESCRIPTION+"</td><td class='has-xml'>"+
				        		riga.HAS_XML+"</td><td class='has-exception'>"+
				        		riga.HAS_EXCEPTION+"</td><td class='xml'>"+
				        		xmlButton +"</td><td class='exception'>"+
				        		exceptionButton +"</td><td>"+
				        		riga.TMS_CREATED+"</td></tr>"
				       			)
				        		.appendTo("#body");
				        		hideIfNull(document.getElementsByClassName("xml")[i], "has-xml");
				        		hideIfNull(document.getElementsByClassName("exception")[i], "has-exception");
						 }
						 document.getElementById('loading-modal').style.display='none';
						 $("table").stupidtable();
				
					    }
					});
				}
				
				function fetchXML(id, type) {
				var querystring="id="+id+"&type="+type;
				if (id == null || type == null) {
					alert("Id or type undefined");
				} else {
					document.getElementById('loading-modal').style.display='block';
					$.ajax({
					    url : '/sys/view_log/query?'+querystring,
						data : '',
						async: true,
						cache : false,
						type : 'get',   
					    dataType : 'json',
					    success: function(xhr) {
					    	$("#xml-text")
					    	.empty();
					    	var xml = xhr[0][Object.keys(xhr[0])[0]];

					    	
					    	$("#xml-text")
					    	.text(xml);
					        document.getElementById('loading-modal').style.display='none';
					        document.getElementById('xml-modal').style.display='block';
					        
					        
					 	}
						});
					}
				}				
				
			function search() {
			  // Declare variables 
			  var input, table, tr, td, i, j, count = 0, pos = 0;
			  input = document.getElementsByClassName("search-input");
                 var l = input.length;
                 
                 var filter = new Array(l);
                 
                 for (i = 0; i < l; i++) {
                 
                 	if(input[i].value !== "") {
			  		filter[i] = input[i].value.toUpperCase();
                   } else {
                   	filter[i] = "NULL";
                   }
                 }
                 
                 
			  table = document.getElementsByTagName("table")[0];
			  tr = table.getElementsByTagName("tr");
			  // Loop through all table rows, and hide those who don't match the search query
			  for (i = 0; i < tr.length; i++) {
			    td = tr[i].getElementsByTagName("td");
                   count=0;
                   pos=0;
			    if (td) {
                     for (j = 0; j < td.length; j++) {
                       if (filter[j] !== "NULL") {
                         count++;
                         if (td[j].innerHTML.toUpperCase().indexOf(filter[j]) > -1) {
                           pos++;
                         }
                       }
                     }
                     if (pos != count) {
                     	tr[i].style.display = "none";
                     } else {
                       tr[i].style.display = "";
                     }
			    } 
			  }
			}
			
			function hideIfNull(element, type) {
				if(element.parentNode.getElementsByClassName(type)[0].innerHTML=='0'){
					element.childNodes[0].style.display='none';
				}
			}
			
			function sort(){
				$(this).parent().stupidsort();
			}
			
			function decode(element){
			try {
			setTimeout(function(){
				var newEl = atob(element.textContent);
				element.textContent = element.textContent.replace(element.textContent,newEl);
				}, 2000);
			} catch (e) {
				alert("Impossibile decodificare il testo - formato non valido");
			} finally {
				$('#loading-modal').hide();
			}
			}
			
			function runSearchOnEnter(e) {
			    if (e.keyCode == 13) {
			        json_ajax();
			    }
			}
        	]]>
			</script>
		</html>
	</xsl:template>
</xsl:transform>