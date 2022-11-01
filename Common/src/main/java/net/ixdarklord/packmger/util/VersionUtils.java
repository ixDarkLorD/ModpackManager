package net.ixdarklord.packmger.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;

public class VersionUtils implements Comparator<String> {
    public static final Logger LOGGER = LogManager.getLogger("ModpackManager/VersionComparator");

    public static void TestingLogger(String version1, String version2) {
        VersionUtils vc = new VersionUtils();
        int result = vc.compare(version1, version2);
        String op = "==";
        if (result < 0) op = "<";
        if (result > 0) op = ">";
        LOGGER.debug(String.format("%s %s %s", version1, op, version2));
    }

    @Override
    public int compare(String version1, String version2) {
        versionSorter sorter1 = new versionSorter(version1);
        versionSorter sorter2 = new versionSorter(version2);

        int number1 = 0, number2 = 0;
        String suffix1 = "", suffix2 = "";

        while (sorter1.MoveNext()) {
            if (!sorter2.MoveNext()) {
                do {
                    number1 = sorter1.getNumber();
                    suffix1 = sorter1.getSuffix();
                    if (number1 != 0 || suffix1.length() != 0) {
                        // Version one is longer than number two, and non-zero
                        return 1;
                    }
                }
                while (sorter1.MoveNext());

                // Version one is longer than version two, but zero
                return 0;
            }

            number1 = sorter1.getNumber();
            suffix1 = sorter1.getSuffix();
            number2 = sorter2.getNumber();
            suffix2 = sorter2.getSuffix();

            if (number1 < number2) {
                // Number one is less than number two
                return -1;
            }
            if (number1 > number2) {
                // Number one is greater than number two
                return 1;
            }

            boolean empty1 = suffix1.length() == 0;
            boolean empty2 = suffix2.length() == 0;

            if (empty1 && empty2) continue; // No suffixes
            if (empty1) return 1; // First suffix is empty (1.2 > 1.2b)
            if (empty2) return -1; // Second suffix is empty (1.2a < 1.2)

            // Lexical comparison of suffixes
            int result = suffix1.compareTo(suffix2);
            if (result != 0) return result;

        }
        if (sorter2.MoveNext()) {
            do {
                number2 = sorter2.getNumber();
                suffix2 = sorter2.getSuffix();
                if (number2 != 0 || suffix2.length() != 0) {
                    // Version one is longer than version two, and non-zero
                    return -1;
                }
            }
            while (sorter2.MoveNext());

            // Version two is longer than version one, but zero
            return 0;
        }
        return 0;
    }

    public static class versionSorter {
        private final String _versionString;
        private final int _length;

        private int _position;
        private int _number;
        private String _suffix;
        private boolean _hasValue;

        public int getNumber() {
            return _number;
        }

        public String getSuffix() {
            return _suffix;
        }

        public boolean hasValue() {
            return _hasValue;
        }

        public versionSorter(String versionString) {
            if (versionString == null)
                throw new IllegalArgumentException("versionString is null");

            _versionString = versionString;
            _length = versionString.length();
        }

        public boolean MoveNext() {
            _number = 0;
            _suffix = "";
            _hasValue = false;

            // No more characters
            if (_position >= _length)
                return false;

            _hasValue = true;

            while (_position < _length) {
                char c = _versionString.charAt(_position);
                if (c < '0' || c > '9') break;
                _number = _number * 10 + (c - '0');
                _position++;
            }

            int suffixStart = _position;

            while (_position < _length) {
                char c = _versionString.charAt(_position);
                if (c == '.' || c == '-' || c == '_') break;
                _position++;
            }

            _suffix = _versionString.substring(suffixStart, _position);

            if (_position < _length) _position++;

            return true;
        }
    }
}
