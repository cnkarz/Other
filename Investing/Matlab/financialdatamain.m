%% FINANCIAL DATA PREDICTION
%     USING REGRESSION WITH REGULARIZATION 
%     AND GRADIENT DESCENT
%
%


clear ; close all; clc; 

% Go to data directory
chdir('d:/Documents/Work/FD');

formatOut = 'yyyymmdd';
today = datestr(now, formatOut)

% Load data to Octave 
%   (blank additional column at the end)
%   (column names taken as 0)


% data = csvread(strcat("financialdata", today, ".csv"));
% data = csvread(strcat('financialdata20170317.csv'));


% Load data without column names and date column 
%   (also no blank column at the end)


 data2 = csvread(strcat('financialdata', today, '.csv'), 1, 1);
% data2 = csvread(strcat('financialdata20170509.csv'), 1, 1);

% Get the class label and X matrix w/o this column (index is at XU030)
% Get 150, 50 and 14 day augmented time windows of the data
%[hx_150] = iterator(data2, 150, 215);
%[hx_50] = iterator(data2, 50, 215);

period1 = 50;    % period used for training
period2 = 15;
pred = 0;       % time lag betwen data and Y 
backdate = 0;

[hx, yy, costArr] = fdp(data2, period1, pred, backdate);
[hx2, yy2, costArr2] = fdp(data2, period2, pred, backdate);
yy(end);
len = length(yy);
len2 = length(hx2);

xHX = (1:len+pred)';
xYY = (1:len)';
xHX2 = ((len+pred-len2+1):len+pred)';
yy(end)

size(xHX2);
% diff = mean(bsxfun (@minus,hy,hx))
% plot(diff)

figure
plot(xYY,yy,'x-',xHX,hx,'-o', xHX, hx2(end-len+1:end), '-*')
title(today)
%  plot(costArr)


% 5. Repeat above steps for various columns (XU030, XAUUSD, EURUSD vs.)
% 5.5 Remove Turkish data for international symbols

% 7. Write to file