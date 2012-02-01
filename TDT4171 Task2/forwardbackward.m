function sv = forwardbackward(ev, prior, T) % ev is the set of evidence for 1..t, pripor is the initial state P(X_0)
  fv(:,:,1) = prior;
  ev_size = size(ev);	% ev is a vector of 1 to t, so t is the length of this vector
  t = ev_size(3);
  b = [1.0  1.0];
  for i = 2.0:t
    b(:,:,i) = [1.0 1.0];	% b is initially a vector of ones, with dimension t    
  end  
  
  sv = [0.0 0.0];
  for i = 2.0:t
    sv(:,:,i) = [0.0 0.0];	% sv is initially a vector of zeroes, with dimension t    
  end  
  
% Calculate forward messages
  for i = 2.0:t
    fv(:,:,i) = forward(fv(:,:,i-1),ev(:,:,i), T);
  end

% Calculate backward messages and perform smoothing
  for i = t:-1.0:2.0
    sv(:,:,i) = normalize(fv(:,:,i).*b(:,:,i));
    b(:,:,i-1) = backward(b(:,:,i), ev(:,:,i), T);
  end

disp('Backward-messages: ');
disp(b);
  
% Last (first) smoothed value not computed in loop
  sv(:,:,1) = normalize(fv(:,1)'.*b(:,:,1));
end