package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocaleUtils {
    private static final ConcurrentMap<String, List<Locale>> cCountriesByLanguage = new ConcurrentHashMap();
    private static final ConcurrentMap<String, List<Locale>> cLanguagesByCountry = new ConcurrentHashMap();

    static class SyncAvoid {
        private static final List<Locale> AVAILABLE_LOCALE_LIST;
        private static final Set<Locale> AVAILABLE_LOCALE_SET;

        SyncAvoid() {
        }

        static {
            List<Locale> list = new ArrayList(Arrays.asList(Locale.getAvailableLocales()));
            AVAILABLE_LOCALE_LIST = Collections.unmodifiableList(list);
            AVAILABLE_LOCALE_SET = Collections.unmodifiableSet(new HashSet(list));
        }
    }

    public static Locale toLocale(String str) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return new Locale("", "");
        }
        if (str.contains("#")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid locale format: ");
            stringBuilder.append(str);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        int len = str.length();
        if (len < 2) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Invalid locale format: ");
            stringBuilder2.append(str);
            throw new IllegalArgumentException(stringBuilder2.toString());
        } else if (str.charAt('\u0000') != '_') {
            return parseLocale(str);
        } else {
            if (len >= 3) {
                char ch1 = str.charAt(1);
                char ch2 = str.charAt(2);
                StringBuilder stringBuilder3;
                if (!Character.isUpperCase(ch1) || !Character.isUpperCase(ch2)) {
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("Invalid locale format: ");
                    stringBuilder3.append(str);
                    throw new IllegalArgumentException(stringBuilder3.toString());
                } else if (len == 3) {
                    return new Locale("", str.substring(1, 3));
                } else {
                    if (len < 5) {
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("Invalid locale format: ");
                        stringBuilder3.append(str);
                        throw new IllegalArgumentException(stringBuilder3.toString());
                    } else if (str.charAt(3) == '_') {
                        return new Locale("", str.substring(1, 3), str.substring(4));
                    } else {
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("Invalid locale format: ");
                        stringBuilder3.append(str);
                        throw new IllegalArgumentException(stringBuilder3.toString());
                    }
                }
            }
            StringBuilder stringBuilder4 = new StringBuilder();
            stringBuilder4.append("Invalid locale format: ");
            stringBuilder4.append(str);
            throw new IllegalArgumentException(stringBuilder4.toString());
        }
    }

    private static Locale parseLocale(String str) {
        if (isISO639LanguageCode(str)) {
            return new Locale(str);
        }
        StringBuilder stringBuilder;
        String[] segments = str.split("_", -1);
        String language = segments[null];
        String country;
        if (segments.length == 2) {
            country = segments[1];
            if ((isISO639LanguageCode(language) && isISO3166CountryCode(country)) || isNumericAreaCode(country)) {
                return new Locale(language, country);
            }
        } else if (segments.length == 3) {
            country = segments[1];
            String variant = segments[2];
            if (isISO639LanguageCode(language) && ((country.length() == 0 || isISO3166CountryCode(country) || isNumericAreaCode(country)) && variant.length() > 0)) {
                return new Locale(language, country, variant);
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid locale format: ");
            stringBuilder.append(str);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid locale format: ");
        stringBuilder.append(str);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private static boolean isISO639LanguageCode(String str) {
        return StringUtils.isAllLowerCase(str) && (str.length() == 2 || str.length() == 3);
    }

    private static boolean isISO3166CountryCode(String str) {
        return StringUtils.isAllUpperCase(str) && str.length() == 2;
    }

    private static boolean isNumericAreaCode(String str) {
        return StringUtils.isNumeric(str) && str.length() == 3;
    }

    public static List<Locale> localeLookupList(Locale locale) {
        return localeLookupList(locale, locale);
    }

    public static List<Locale> localeLookupList(Locale locale, Locale defaultLocale) {
        List<Locale> list = new ArrayList(4);
        if (locale != null) {
            list.add(locale);
            if (locale.getVariant().length() > 0) {
                list.add(new Locale(locale.getLanguage(), locale.getCountry()));
            }
            if (locale.getCountry().length() > 0) {
                list.add(new Locale(locale.getLanguage(), ""));
            }
            if (!list.contains(defaultLocale)) {
                list.add(defaultLocale);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static List<Locale> availableLocaleList() {
        return SyncAvoid.AVAILABLE_LOCALE_LIST;
    }

    public static Set<Locale> availableLocaleSet() {
        return SyncAvoid.AVAILABLE_LOCALE_SET;
    }

    public static boolean isAvailableLocale(Locale locale) {
        return availableLocaleList().contains(locale);
    }

    public static List<Locale> languagesByCountry(String countryCode) {
        if (countryCode == null) {
            return Collections.emptyList();
        }
        List<Locale> langs = (List) cLanguagesByCountry.get(countryCode);
        if (langs == null) {
            ArrayList langs2 = new ArrayList();
            for (Locale locale : availableLocaleList()) {
                if (countryCode.equals(locale.getCountry()) && locale.getVariant().isEmpty()) {
                    langs2.add(locale);
                }
            }
            cLanguagesByCountry.putIfAbsent(countryCode, Collections.unmodifiableList(langs2));
            langs = (List) cLanguagesByCountry.get(countryCode);
        }
        return langs;
    }

    public static List<Locale> countriesByLanguage(String languageCode) {
        if (languageCode == null) {
            return Collections.emptyList();
        }
        List<Locale> countries = (List) cCountriesByLanguage.get(languageCode);
        if (countries == null) {
            ArrayList countries2 = new ArrayList();
            for (Locale locale : availableLocaleList()) {
                if (languageCode.equals(locale.getLanguage()) && locale.getCountry().length() != 0 && locale.getVariant().isEmpty()) {
                    countries2.add(locale);
                }
            }
            cCountriesByLanguage.putIfAbsent(languageCode, Collections.unmodifiableList(countries2));
            countries = (List) cCountriesByLanguage.get(languageCode);
        }
        return countries;
    }
}
