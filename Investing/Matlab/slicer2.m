%% 
%   FINANCIAL DATA PREDICTION
%     USING REGRESSION WITH REGULARIZATION 
%     AND GRADIENT DESCENT
%%

% This function gives the augmented subsets 
%    of the matrix for required time windows.

function [Y, Xslice] = slicer2(data, index, predictors, backdate)
 
    % starting from available data for all symbols
    start = 2800;    
    
    % csvwrite('tester.csv',normalizer(data(2800:end,:)));
    % -1s due to empty last rows
    Y = data(2800:end-backdate, index);%2800

    
    Xslice_pre = data(2800:end-backdate,:);%2800
 
    Xslice_pre = Xslice_pre(:,predictors);
  
    bias = ones(size(Xslice_pre,1),1);
    Xslice = [bias Xslice_pre];
  
end
  