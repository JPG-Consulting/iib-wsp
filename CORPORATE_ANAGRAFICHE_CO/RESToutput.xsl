<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:io="http://www.gruppoiren.it/AnagraficheCO/" version="2.0">
	<xsl:output method="html" encoding="UTF-8" indent="yes"
		omit-xml-declaration="yes" />

	<xsl:template match="/io:Output">
		<html>
			<head>
				<style>
					table {
					font-family: arial, sans-serif;
					border-collapse:
					collapse;
					width: 100%;
					}

					td {
					border: 1px solid #dddddd;
					text-align:
					left;
					padding: 6px;
					font-size: 80%;
					}
					th {
					border: 1px solid #dddddd;
					text-align: left;
					padding: 6px;
					font-size: 120%;
					}

					input {
					border: 0px solid #dddddd;
					font-size: 80%;
					margin: 1px;
					}
					th[data-sort]{
					cursor:pointer;
					}
					tbody tr:hover {
                      background-color: #dddddd;
                    }
                    tbody tr.selected {
                      background-color: #dddddd;
                    }
				</style>

				<script
					src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
				<script>
					$(function(){
					$("table").stupidtable();
					});
				</script>
				<script>
				$(function(){
					$("tbody tr").click(function() {
                    	$(this).toggleClass('selected').siblings().removeClass("selected");
					});
				});
				</script>
			</head>
			<body>

				<table>
					<thead>
						<th data-sort="string">
							Oggetto contabile (<xsl:value-of select="oggettoContabile/@num" />)(<xsl:value-of select="oggettoContabile/@tipo" />)
						</th>
						<th data-sort="string">Descrizione</th>
						<th data-sort="string">Societa</th>
						<th data-sort="string">DataInizioValidita</th>
						<th data-sort="string">DataFineValidita</th>
					</thead>
					<thead>
						<th>
							<input type="text" id="0" onkeyup="search()" placeholder="Ricerca per codice..."
								title="Digita una stringa" />
						</th>
						<th>
							<input type="text" id="1" onkeyup="search()"
								placeholder="Ricerca per descrizione..." title="Digita una stringa" />
						</th>
						<th>
							<input type="text" id="2" onkeyup="search()" placeholder="Ricerca per società..."
								title="Digita una stringa" />
						</th>
						<th>
							<input type="text" id="3" onkeyup="search()"
								placeholder="Ricerca per data inizio..." title="Digita una stringa" />
						</th>
						<th>
							<input type="text" id="4" onkeyup="search()"
								placeholder="Ricerca per data fine..." title="Digita una stringa" />
						</th>
					</thead>
					<tbody>
						<xsl:for-each select="oggettoContabile">
							<tr>
								<td>
									<xsl:value-of select="codice" />
								</td>
								<td>
									<xsl:value-of select="descrizione" />
								</td>
								<td>
									<xsl:value-of select="societa" />
								</td>
								<td>
									<xsl:value-of select="dataInizioValidita" />
								</td>
								<td>
									<xsl:value-of select="dataFineValidita" />
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
			</body>
			<script language="javascript"><![CDATA[
			(function($) {
				  $.fn.stupidtable = function(sortFns) {
				    return this.each(function() {
				      var $table = $(this);
				      sortFns = sortFns || {};
				      sortFns = $.extend({}, $.fn.stupidtable.default_sort_fns, sortFns);
				      $table.data('sortFns', sortFns);
				
				      $table.on("click.stupidtable", "thead th", function() {
				          $(this).stupidsort();
				      });
				    });
				  };
				
				
				  // Expects $("#mytable").stupidtable() to have already been called.
				  // Call on a table header.
				  $.fn.stupidsort = function(force_direction){
				    var $this_th = $(this);
				    var th_index = 0; // we'll increment this soon
				    var dir = $.fn.stupidtable.dir;
				    var $table = $this_th.closest("table");
				    var datatype = $this_th.data("sort") || null;
				
				    // No datatype? Nothing to do.
				    if (datatype === null) {
				      return;
				    }
				
				    // Account for colspans
				    $this_th.parents("tr").find("th").slice(0, $(this).index()).each(function() {
				      var cols = $(this).attr("colspan") || 1;
				      th_index += parseInt(cols,10);
				    });
				
				    var sort_dir;
				    if(arguments.length == 1){
				        sort_dir = force_direction;
				    }
				    else{
				        sort_dir = force_direction || $this_th.data("sort-default") || dir.ASC;
				        if ($this_th.data("sort-dir"))
				           sort_dir = $this_th.data("sort-dir") === dir.ASC ? dir.DESC : dir.ASC;
				    }
				
				    // Bail if already sorted in this direction
				    if ($this_th.data("sort-dir") === sort_dir) {
				      return;
				    }
				    // Go ahead and set sort-dir.  If immediately subsequent calls have same sort-dir they will bail
				    $this_th.data("sort-dir", sort_dir);
				
				    $table.trigger("beforetablesort", {column: th_index, direction: sort_dir});
				
				    // More reliable method of forcing a redraw
				    $table.css("display");
				
				    // Run sorting asynchronously on a timout to force browser redraw after
				    // `beforetablesort` callback. Also avoids locking up the browser too much.
				    setTimeout(function() {
				      // Gather the elements for this column
				      var column = [];
				      var sortFns = $table.data('sortFns');
				      var sortMethod = sortFns[datatype];
				      var trs = $table.children("tbody").children("tr");
				
				      // Extract the data for the column that needs to be sorted and pair it up
				      // with the TR itself into a tuple. This way sorting the values will
				      // incidentally sort the trs.
				      trs.each(function(index,tr) {
				        var $e = $(tr).children().eq(th_index);
				        var sort_val = $e.data("sort-value");
				
				        // Store and read from the .data cache for display text only sorts
				        // instead of looking through the DOM every time
				        if(typeof(sort_val) === "undefined"){
				          var txt = $e.text();
				          $e.data('sort-value', txt);
				          sort_val = txt;
				        }
				        column.push([sort_val, tr]);
				      });
				
				      // Sort by the data-order-by value
				      column.sort(function(a, b) { return sortMethod(a[0], b[0]); });
				      if (sort_dir != dir.ASC)
				        column.reverse();
				
				      // Replace the content of tbody with the sorted rows. Strangely
				      // enough, .append accomplishes this for us.
				      trs = $.map(column, function(kv) { return kv[1]; });
				      $table.children("tbody").append(trs);
				
				      // Reset siblings
				      $table.find("th").data("sort-dir", null).removeClass("sorting-desc sorting-asc");
				      $this_th.data("sort-dir", sort_dir).addClass("sorting-"+sort_dir);
				
				      $table.trigger("aftertablesort", {column: th_index, direction: sort_dir});
				      $table.css("display");
				    }, 10);
				
				    return $this_th;
				  };
				
				  // Call on a sortable td to update its value in the sort. This should be the
				  // only mechanism used to update a cell's sort value. If your display value is
				  // different from your sort value, use jQuery's .text() or .html() to update
				  // the td contents, Assumes stupidtable has already been called for the table.
				  $.fn.updateSortVal = function(new_sort_val){
				  var $this_td = $(this);
				    if($this_td.is('[data-sort-value]')){
				      // For visual consistency with the .data cache
				      $this_td.attr('data-sort-value', new_sort_val);
				    }
				    $this_td.data("sort-value", new_sort_val);
				    return $this_td;
				  };
				
				  // ------------------------------------------------------------------
				  // Default settings
				  // ------------------------------------------------------------------
				  $.fn.stupidtable.dir = {ASC: "asc", DESC: "desc"};
				  $.fn.stupidtable.default_sort_fns = {
				    "int": function(a, b) {
				      return parseInt(a, 10) - parseInt(b, 10);
				    },
				    "float": function(a, b) {
				      return parseFloat(a) - parseFloat(b);
				    },
				    "string": function(a, b) {
				      return a.toString().localeCompare(b.toString());
				    },
				    "string-ins": function(a, b) {
				      a = a.toString().toLocaleLowerCase();
				      b = b.toString().toLocaleLowerCase();
				      return a.localeCompare(b);
				    }
				  };
				})(jQuery);


			function search() {
			  // Declare variables 
			  var input, table, tr, td, i, j, count = 0, pos = 0;
			  input = document.getElementsByTagName("input");
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
        	]]>
			</script>
		</html>
	</xsl:template>
</xsl:transform>