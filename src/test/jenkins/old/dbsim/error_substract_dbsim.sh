#save_results() {
	# find out the errors and cut a snapshot for the errors
	
	#rm -f $1
	rm -f $2
	
	ls $1 >& list.txt

	#error_exprs="/warn/I= /exception/I= /error/I= /fail/I="
        exception_exprs="setting je.lock.oldLockExceptions to true"
	error_exprs="exception error fail"
	while read LINE
	do
		start_pos=0

		# find the first position where an
		for fail_message in $error_exprs
		do
			#sed -n "$fail_message" $LINE >& fails.txt
                        grep -n -i "$fail_message" $LINE | grep -v "$exception_exprs" | cut -d ":" -f 1 > fails.txt
                        #echo $fail_message
                        #cat fails.txt
			err_pos=`sed -n '1p' fails.txt`
			if [ "$err_pos" = "" ]; then
				err_pos=0
			fi

			if [ "$err_pos" != 0 ]; then
				if [ "$start_pos" = 0 ]; then
					start_pos=$err_pos
				elif [ "$start_pos" -gt 0 ]; then
					if [ "$start_pos" -ge "$err_pos" ]; then
						start_pos=$err_pos
					fi
				fi
			fi
			rm -rf fails.txt
		done

		# cut the following 1000 lines as the err log
		if [ "$start_pos" != 0 ]; then
			exist_failure="true"
			end_pos=`expr $start_pos + 10`
			#result=`echo "$LINE" | sed 's/.txt$//g'`
			data=`sed -n "$start_pos"','"$end_pos"'p' $LINE`
			message=`sed -n "$start_pos"','"$start_pos"'p' $LINE`
			#bash ~/gen_xml.sh $3 $4 $5 1 1 "$message" "$data" "$6" 
                        message=${message//\&/&amp;}
                        message=${message//\'/&apos;}
                        message=${message//\"/&quot;}
                        message=${message//\</&lt;}
                        message=${message//\>/&gt;}

			if [ "${7}" == "true" ] ; then
				bash ~/gen_xml.sh "$3" "${message}" "$5" 1 1 "$message" "$data" "$6" 
			else
				bash ~/gen_xml.sh "$3" "$4" "$5" 1 1 "$message" "$data" "$6" 
			fi
		fi
		if [ "$start_pos" == 0 ]; then
			bash ~/gen_xml.sh "$3" "$4" "$5" 1 0 "" "" "$6"
		fi
	done < list.txt

	# save error log to server
	if [ "$exist_failure" = "true" ]; then
		ls *.log >& list.txt
		#scp list.txt jenkins@slc04ark:~/jobs/je_dbsim/workspace/

		#while read LINE
		#do
			#scp $LINE jenkins@slc04ark:~/jobs/je_dbsim/workspace/
		#done < list.txt
	fi
#}

