function [ TABLE ] = read_ranging_data()
	!/Developer/SDKs/platform-tools/adb pull /storage/emulated/0/FIRST/ranging_data.dat
	!cat ./ranging_data.dat|grep -v "NewVal\|AvgVal" > ./ranging_data.dat1
	!mv ./ranging_data.dat1 ./ranging_data.dat
	fid = fopen('./ranging_data.dat','r');
	tline = regexprep(fgetl(fid),',','');
	while ischar(tline);
		
		if size(tline,2) > 0
			VALS = cell2table(strsplit(tline));
			VALS.Properties.VariableNames = {'Raw' 'Avg'};
			if ~exist('TABLE','var')
				TABLE = VALS;
			else
				TABLE = [TABLE;VALS];
			end
		end
		tline = fgetl(fid);
	end
	TABLE.Raw = str2double(TABLE.Raw);
	TABLE.Avg = str2double(TABLE.Avg);
end

