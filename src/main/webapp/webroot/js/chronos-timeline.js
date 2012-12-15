(function TimelineViewModel() {

	var self = this;
	self.username = ko.observable('');
	self.password = ko.observable('');
	self.sessionID = ko.observable('');
	self.dateEventIndex = 0;
	self.timelineEvents = null;
	self.updateFlipCounter = animateFlipCounter;
		
	chronos.addInitializer(function () {
		ko.applyBindings(self);
		
		$.getJSON("/timeline.json", function(data) {
			if (data) {
				self.timelineEvents = data.timeline.date;
			}
		});
		
		checkIfUserIsAuthorised();
		
		checkIfCounterUsed();
		
		loadTimeline();
	});
	
	self.login = function() {
		chronos.login(self.username(), self.password(), function(reply) {
			if (reply.status === 'ok') {
				self.sessionID(reply.sessionID);
				window.location.href = window.location.origin + 
					"/admin/index.html?sessionID=" + reply.sessionID +
					"&username=" + self.username();
			} else {
				$('#loginErrorModal').modal('show');
			}
		});
	}	
	
	self.logout = function() {
		chronos.logout(function(reply) {
			self.sessionID(null);
			window.location.href = window.location.origin + "/";
		});
	}
	
	function loadTimeline() {
		chronos.find('timeline', {}, function(reply) {
			if (reply.results.length === 1) {
				var json = JSON.stringify({ timeline: reply.results[0] });
				createStoryJS({
					width:  '100%',
					height: '100%',
					lang:   'pt',
					source: 'timeline.json'
				});
			}
		});
	}
	
	function registerNavigationListeners() {
		setTimeout(function() {
	        if ($('.nav-next').exists() && $('.nav-previous').exists()) {
	          $('.nav-next').on('click', function () {
	        	  if (self.dateEventIndex < self.timelineEvents.length) {
	        		  self.updateFlipCounter($("#counter").flipCounter("getNumber") + parseInt(self.timelineEvents[self.dateEventIndex].amount));
		        	  self.dateEventIndex++;
	        	  }
	          });
	          $('.nav-previous').on('click', function () {
	        	  if (self.dateEventIndex >= 0) {
	        		  self.dateEventIndex--;
	        		  self.updateFlipCounter($("#counter").flipCounter("getNumber") - parseInt(self.timelineEvents[self.dateEventIndex].amount));
	        	  }
	          });
	          
	          $('.flag-content').bind('click', function() {
	        	  self.dateEventIndex = $.inArray(this, $('.flag-content'));
	        	  
	        	  var total = 0;
	        	  for (var index = 0; index < self.dateEventIndex; index++) {
	        		  total += parseInt(self.timelineEvents[index].amount);
	        	  }
	        	  
	        	  self.updateFlipCounter(total);	        	  
	          });

	        } else {
	          setTimeout(arguments.callee, 100);
	        }
	      },100);
	}
	
	function stepFlipCounter(toValue) {
		$("#counter").flipCounter("setNumber", toValue);
	}
	
	function animateFlipCounter(toValue) {
  	  $("#counter").flipCounter(
  			  "startAnimation", {
  				  number: $("#counter").flipCounter("getNumber"),
		          end_number: toValue
		        }
		);

	}
	
	function checkIfUserIsAuthorised() {
		var u = chronos.getUsername();
		var s = chronos.getSessionID();		
		if (u !== undefined && s !== undefined) {
			chronos.authoriseSession(s, function(reply) {
				if (reply.status === 'ok') {
					self.username(u);
					self.sessionID(s);
					$('#loginForm').hide();
					$('#sessionControl').show();
					$('#navigator').show();
				} else {
					$('#loginErrorModal').modal('show');
				}
			});	
		}		
	}
	
	function checkIfCounterUsed() {
		chronos.find('timeline.options', {}, function(reply) {
			if (reply.results.length === 1) {
				self.options = reply.results[0];
				if (self.options.useCounter === true) {
					registerNavigationListeners();
					$("#counter").flipCounter({
				        number:0, // the initial number the counter should display, overrides the hidden field
				        numIntegralDigits:1, // number of places left of the decimal point to maintain
				        numFractionalDigits:0, // number of places right of the decimal point to maintain
				        digitClass:"counter-digit", // class of the counter digits
				        counterFieldName:"counter-value", // name of the hidden field
				        digitHeight:40, // the height of each digit in the flipCounter-medium.png sprite image
				        digitWidth:30, // the width of each digit in the flipCounter-medium.png sprite image
				        imagePath:"img/flipCounter-medium.png", // the path to the sprite image relative to your html document
				        easing: 'linear', // the easing function to apply to animations, you can override this with a jQuery.easing method
				        duration: self.options.animationDelay, // duration of animations
				        onAnimationStarted:false, // call back for animation upon starting
				        onAnimationStopped:false, // call back for animation upon stopping
				        onAnimationPaused:false, // call back for animation upon pausing
				        onAnimationResumed:false, // call back for animation upon resuming from pause
				        formatNumberOptions:{format:"###,###,###",locale:"pt"}
					});
					
					if (self.options.animateCounter === false) {
						self.updateFlipCounter = stepFlipCounter;
					}
					$('#counterBox').show();					
				} else {
					$('#counterBox').hide();
				}
			}
		});
	}
	
})();
