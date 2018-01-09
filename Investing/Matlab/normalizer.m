%% 
%   FINANCIAL DATA PREDICTION
%     USING REGRESSION WITH REGULARIZATION 
%     AND GRADIENT DESCENT
%%

% This function normalizes the matrix (feature scaling).

function Xnorm = normalizer(X)

  Xmain = X(:,2:end);
  
  xmean = mean(Xmain);
  xsigma = std(Xmain);
  
  Xtemp = bsxfun(@rdivide, bsxfun(@minus, Xmain, xmean), xsigma);
  Xnorm = [X(:,1) Xtemp];

end
    