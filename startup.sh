#!/bin/bash
set -e
 
export DISPLAY=:99
 
echo ">>> Starting Xvfb..."
Xvfb :99 -screen 0 1920x1080x24 &
sleep 2
echo "✅ Xvfb running on :99"
 
echo ">>> Starting x11vnc..."
x11vnc -display :99 -forever -nopw -rfbport 5900 -shared > /app/x11vnc.log 2>&1 &
sleep 2
echo "✅ x11vnc running on 5900"
 
echo ">>> Starting noVNC..."
/app/novnc/utils/novnc_proxy --vnc localhost:5900 --listen 6080 > /app/novnc.log 2>&1 &
sleep 2
echo "✅ noVNC running on 6080"
 
echo ">>> Starting XFCE Desktop..."
startxfce4 &
sleep 5
echo "✅ XFCE started on :99"
 
# ---------------------------
# Run Maven tests inside XFCE terminal
# ---------------------------
echo ">>> Running Selenium tests..."
xfce4-terminal --hold -e "bash -c 'cd /app && mvn test; exec bash'" &
 
# ---------------------------
# Wait for reports and open in Chrome
# ---------------------------
(
  sleep 20
  latest_report=$(ls -td /app/features/TestReports/TestReport_* 2>/dev/null | head -1)
  if [ -n "$latest_report" ] && [ -f "$latest_report/report" ]; then
    echo ">>> Opening generated report in Chrome..."
    google-chrome --no-sandbox --disable-gpu "$latest_report/report" &
  else
    echo "⚠️ No report found yet. Check Maven logs."
  fi
) &
 
# Keep container alive
tail -f /dev/null
